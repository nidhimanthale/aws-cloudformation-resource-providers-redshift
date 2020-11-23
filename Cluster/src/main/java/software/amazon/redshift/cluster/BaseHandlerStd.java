package software.amazon.redshift.cluster;

import com.amazonaws.SdkClientException;
import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.ClusterNotFoundException;
import software.amazon.awssdk.services.redshift.model.CreateClusterRequest;
import software.amazon.awssdk.services.redshift.model.DescribeClustersRequest;
import software.amazon.awssdk.services.redshift.model.DescribeClustersResponse;
import software.amazon.awssdk.services.redshift.model.ModifyClusterIamRolesResponse;
import software.amazon.awssdk.services.redshift.model.RedshiftException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

// Placeholder for the functionality that could be shared across Create/Read/Update/Delete/List Handlers

public abstract class BaseHandlerStd extends BaseHandler<CallbackContext> {
  @Override
  public final ProgressEvent<ResourceModel, CallbackContext> handleRequest(
    final AmazonWebServicesClientProxy proxy,
    final ResourceHandlerRequest<ResourceModel> request,
    final CallbackContext callbackContext,
    final Logger logger) {
    return handleRequest(
      proxy,
      request,
      callbackContext != null ? callbackContext : new CallbackContext(),
      proxy.newProxy(ClientBuilder::getClient),
      logger
    );
  }

  protected abstract ProgressEvent<ResourceModel, CallbackContext> handleRequest(
    final AmazonWebServicesClientProxy proxy,
    final ResourceHandlerRequest<ResourceModel> request,
    final CallbackContext callbackContext,
    final ProxyClient<RedshiftClient> proxyClient,
    final Logger logger);


  protected boolean isClusterActive (final ProxyClient<RedshiftClient> proxyClient, ResourceModel model, CallbackContext cxt) {
    String clusterIdentifier = StringUtils.isNullOrEmpty(model.getNewClusterIdentifier())
            ? model.getClusterIdentifier() : model.getNewClusterIdentifier();

    // Patching WF starts with a slight delay, adding sleep to pass contract tests
    if(model.getRedshiftCommand()!= null && model.getRedshiftCommand().equals("modify-cluster-db-revision")) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    DescribeClustersRequest awsRequest =
            DescribeClustersRequest.builder().clusterIdentifier(clusterIdentifier).build();
    DescribeClustersResponse awsResponse =
            proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::describeClusters);

    return awsResponse.clusters().get(0).clusterStatus().equals("available");
  }

  protected boolean isClusterPaused (final ProxyClient<RedshiftClient> proxyClient, ResourceModel model, CallbackContext cxt) {
    String clusterIdentifier = StringUtils.isNullOrEmpty(model.getNewClusterIdentifier())
            ? model.getClusterIdentifier() : model.getNewClusterIdentifier();

    DescribeClustersRequest awsRequest =
            DescribeClustersRequest.builder().clusterIdentifier(clusterIdentifier).build();
    DescribeClustersResponse awsResponse =
            proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::describeClusters);

    return awsResponse.clusters().get(0).clusterStatus().equals("paused");
  }

  protected boolean isClusterAvailableForUpdate (final ProxyClient<RedshiftClient> proxyClient, ResourceModel model,
                                                 String clusterIdentifier) {
    DescribeClustersResponse awsResponse = null;
    DescribeClustersRequest awsRequest =
            DescribeClustersRequest.builder().clusterIdentifier(clusterIdentifier).build();
    try {
      awsResponse = proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::describeClusters);
    } catch (final ClusterNotFoundException e) {
        if (!StringUtils.isNullOrEmpty(model.getNewClusterIdentifier())) {
          return isClusterAvailableForUpdate(proxyClient, model, model.getNewClusterIdentifier());
        }
        return false;
    }
    return true;
  }

  // check for required parameters to not have null values
  protected boolean invalidCreateClusterRequest(ResourceModel model) {
    return model.getClusterIdentifier() == null || model.getNodeType() == null
            || model.getMasterUsername() == null || model.getMasterUserPassword() == null;
  }
}
