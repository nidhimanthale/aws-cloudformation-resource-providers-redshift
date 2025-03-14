package software.amazon.redshift.cluster;

import com.amazonaws.arn.Arn;
import com.amazonaws.util.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cloudwatch.model.InvalidParameterValueException;
import software.amazon.awssdk.services.redshift.RedshiftClient;
import software.amazon.awssdk.services.redshift.model.BucketNotFoundException;
import software.amazon.awssdk.services.redshift.model.ClusterAlreadyExistsException;
import software.amazon.awssdk.services.redshift.model.ClusterNotFoundException;
import software.amazon.awssdk.services.redshift.model.ClusterOnLatestRevisionException;
import software.amazon.awssdk.services.redshift.model.ClusterParameterGroupNotFoundException;
import software.amazon.awssdk.services.redshift.model.ClusterSecurityGroupNotFoundException;
import software.amazon.awssdk.services.redshift.model.ClusterSubnetQuotaExceededException;
import software.amazon.awssdk.services.redshift.model.CopyToRegionDisabledException;
import software.amazon.awssdk.services.redshift.model.CreateTagsRequest;
import software.amazon.awssdk.services.redshift.model.CreateTagsResponse;
import software.amazon.awssdk.services.redshift.model.DeleteResourcePolicyRequest;
import software.amazon.awssdk.services.redshift.model.DeleteResourcePolicyResponse;
import software.amazon.awssdk.services.redshift.model.DeleteTagsRequest;
import software.amazon.awssdk.services.redshift.model.DeleteTagsResponse;
import software.amazon.awssdk.services.redshift.model.DependentServiceRequestThrottlingException;
import software.amazon.awssdk.services.redshift.model.DescribeClustersRequest;
import software.amazon.awssdk.services.redshift.model.DescribeClustersResponse;
import software.amazon.awssdk.services.redshift.model.DisableLoggingRequest;
import software.amazon.awssdk.services.redshift.model.DisableLoggingResponse;
import software.amazon.awssdk.services.redshift.model.DisableSnapshotCopyRequest;
import software.amazon.awssdk.services.redshift.model.DisableSnapshotCopyResponse;
import software.amazon.awssdk.services.redshift.model.EnableLoggingRequest;
import software.amazon.awssdk.services.redshift.model.EnableLoggingResponse;
import software.amazon.awssdk.services.redshift.model.EnableSnapshotCopyRequest;
import software.amazon.awssdk.services.redshift.model.EnableSnapshotCopyResponse;
import software.amazon.awssdk.services.redshift.model.FailoverPrimaryComputeRequest;
import software.amazon.awssdk.services.redshift.model.FailoverPrimaryComputeResponse;
import software.amazon.awssdk.services.redshift.model.HsmClientCertificateNotFoundException;
import software.amazon.awssdk.services.redshift.model.HsmConfigurationNotFoundException;
import software.amazon.awssdk.services.redshift.model.IncompatibleOrderableOptionsException;
import software.amazon.awssdk.services.redshift.model.InsufficientClusterCapacityException;
import software.amazon.awssdk.services.redshift.model.InsufficientS3BucketPolicyException;
import software.amazon.awssdk.services.redshift.model.InvalidClusterSecurityGroupStateException;
import software.amazon.awssdk.services.redshift.model.InvalidClusterStateException;
import software.amazon.awssdk.services.redshift.model.InvalidClusterTrackException;
import software.amazon.awssdk.services.redshift.model.InvalidElasticIpException;
import software.amazon.awssdk.services.redshift.model.InvalidPolicyException;
import software.amazon.awssdk.services.redshift.model.InvalidRetentionPeriodException;
import software.amazon.awssdk.services.redshift.model.InvalidS3BucketNameException;
import software.amazon.awssdk.services.redshift.model.InvalidS3KeyPrefixException;
import software.amazon.awssdk.services.redshift.model.InvalidTagException;
import software.amazon.awssdk.services.redshift.model.LimitExceededException;
import software.amazon.awssdk.services.redshift.model.ModifyAquaConfigurationRequest;
import software.amazon.awssdk.services.redshift.model.ModifyAquaConfigurationResponse;
import software.amazon.awssdk.services.redshift.model.ModifyClusterDbRevisionRequest;
import software.amazon.awssdk.services.redshift.model.ModifyClusterDbRevisionResponse;
import software.amazon.awssdk.services.redshift.model.ModifyClusterIamRolesRequest;
import software.amazon.awssdk.services.redshift.model.ModifyClusterIamRolesResponse;
import software.amazon.awssdk.services.redshift.model.ModifyClusterMaintenanceRequest;
import software.amazon.awssdk.services.redshift.model.ModifyClusterMaintenanceResponse;
import software.amazon.awssdk.services.redshift.model.ModifyClusterRequest;
import software.amazon.awssdk.services.redshift.model.ModifyClusterResponse;
import software.amazon.awssdk.services.redshift.model.ModifySnapshotCopyRetentionPeriodRequest;
import software.amazon.awssdk.services.redshift.model.ModifySnapshotCopyRetentionPeriodResponse;
import software.amazon.awssdk.services.redshift.model.NumberOfNodesPerClusterLimitExceededException;
import software.amazon.awssdk.services.redshift.model.NumberOfNodesQuotaExceededException;
import software.amazon.awssdk.services.redshift.model.PauseClusterRequest;
import software.amazon.awssdk.services.redshift.model.PauseClusterResponse;
import software.amazon.awssdk.services.redshift.model.PutResourcePolicyRequest;
import software.amazon.awssdk.services.redshift.model.PutResourcePolicyResponse;
import software.amazon.awssdk.services.redshift.model.RebootClusterRequest;
import software.amazon.awssdk.services.redshift.model.RebootClusterResponse;
import software.amazon.awssdk.services.redshift.model.RedshiftException;
import software.amazon.awssdk.services.redshift.model.ResizeClusterRequest;
import software.amazon.awssdk.services.redshift.model.ResizeClusterResponse;
import software.amazon.awssdk.services.redshift.model.ResourceNotFoundException;
import software.amazon.awssdk.services.redshift.model.ResumeClusterRequest;
import software.amazon.awssdk.services.redshift.model.ResumeClusterResponse;
import software.amazon.awssdk.services.redshift.model.RotateEncryptionKeyRequest;
import software.amazon.awssdk.services.redshift.model.RotateEncryptionKeyResponse;
import software.amazon.awssdk.services.redshift.model.SnapshotCopyAlreadyDisabledException;
import software.amazon.awssdk.services.redshift.model.SnapshotCopyAlreadyEnabledException;
import software.amazon.awssdk.services.redshift.model.SnapshotCopyDisabledException;
import software.amazon.awssdk.services.redshift.model.SnapshotCopyGrantNotFoundException;
import software.amazon.awssdk.services.redshift.model.TableLimitExceededException;
import software.amazon.awssdk.services.redshift.model.TagLimitExceededException;
import software.amazon.awssdk.services.redshift.model.UnauthorizedOperationException;
import software.amazon.awssdk.services.redshift.model.UnknownSnapshotCopyRegionException;
import software.amazon.awssdk.services.redshift.model.UnsupportedOperationException;
import software.amazon.awssdk.services.redshift.model.UnsupportedOptionException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Optional;
import java.util.HashMap;
import java.util.stream.Stream;

public class UpdateHandler extends BaseHandlerStd {
    private Logger logger;
    private final String RESOURCE_NAME_PREFIX = "arn:aws:redshift:";

    /*
    Any 1 or 1+ attribute(s) value(s) change(s) will trigger a modifyClusterRequest,
    regardless of sensitive or insensitive.

    Detectable meaning we only support modifyClusterRequest if the included attributes in Cluster model change.
     */
    public static final String[] DETECTABLE_MODIFY_CLUSTER_ATTRIBUTES_INSENSITIVE = new String[] {
            "AllowVersionUpgrade",
            "AutomatedSnapshotRetentionPeriod",
            "AvailabilityZone",
            "AvailabilityZoneRelocation",
            "ClusterSecurityGroups",
            "ClusterVersion",
            "ElasticIp",
            "Encrypted",
            "EnhancedVpcRouting",
            "HsmClientCertificateIdentifier",
            "HsmConfigurationIdentifier",
            "KmsKeyId",
            "MaintenanceTrackName",
            "ManualSnapshotRetentionPeriod",
            "Port",
            "PreferredMaintenanceWindow",
            "PubliclyAccessible",
            "VpcSecurityGroupIds",
            "MultiAZ"
    };
    public static final String[] DETECTABLE_MODIFY_CLUSTER_ATTRIBUTES_SENSITIVE = new String[] {
            "MasterUserPassword"
    };

    protected ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<RedshiftClient> proxyClient,
            final Logger logger) {

        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();

        Map<String, String> allDesiredTags = new HashMap<>();
        allDesiredTags.putAll(Optional.ofNullable(request.getDesiredResourceTags()).orElse(Collections.emptyMap()));
        allDesiredTags.putAll(Optional.ofNullable(
                Translator.translateFromResourceModelToSdkTags(model.getTags()))
                .orElse(Collections.emptyMap()));

        Map<String, String> allPreviousTags = new HashMap<>();
        allPreviousTags.putAll(Optional.ofNullable(request.getPreviousResourceTags()).orElse(Collections.emptyMap()));
        allPreviousTags.putAll(Optional.ofNullable(Translator.translateFromResourceModelToSdkTags(
                request.getPreviousResourceState().getTags())).orElse(Collections.emptyMap()));

        if (!callbackContext.getClusterExistsCheck()) {
            boolean clusterExists = doesClusterExist(proxyClient, model, model.getClusterIdentifier());
            callbackContext.setClusterExistsCheck(true);
                if(!clusterExists) {
                    return ProgressEvent.<ResourceModel, CallbackContext>builder()
                            .status(OperationStatus.FAILED)
                            .errorCode(HandlerErrorCode.NotFound)
                            .message(String.format("Cluster %s Not Found %s", model.getClusterIdentifier(),HandlerErrorCode.NotFound.getMessage()))
                            .build();
            }
        }

        //Redshift is Driftable
        if(request.getDriftable() != null && request.getDriftable().equals(true)) {
            logger.log(String.format("%s %s is Drifted", ResourceModel.TYPE_NAME, model.getClusterIdentifier()));
            if(isRebootRequired(model, proxyClient)) {
                return proxy.initiate("AWS-Redshift-Cluster::RebootCluster", proxyClient, model, callbackContext)
                        .translateToServiceRequest(Translator::translateToRebootClusterRequest)
                        .makeServiceCall(this::rebootCluster)
                        .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                        .progress();
            }
        }

        return ProgressEvent.progress(model, callbackContext)
                .then(progress -> {
                    if (model.getResourceAction() != null && RESUME_CLUSTER.equals(model.getResourceAction()) &&
                            PAUSE_CLUSTER.equals(request.getPreviousResourceState().getResourceAction())) {
                        return proxy.initiate("AWS-Redshift-Cluster::ResumeCluster", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToResumeClusterRequest)
                                .makeServiceCall(this::resumeCluster)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();
                    }
                    return progress;
                })

                .then(progress -> {
                    List<List<Tag>> updateTags = updateTags(Translator.translateTagsMapToTagCollection(allPreviousTags),
                            Translator.translateTagsMapToTagCollection(allDesiredTags));

                    final String resourceName = Arn.builder()
                            .withService("redshift")
                            .withPartition(request.getAwsPartition())
                            .withAccountId(request.getAwsAccountId())
                            .withRegion(request.getRegion())
                            .withResource(String.format("cluster:%s", model.getClusterIdentifier()))
                            .build()
                            .toString();

                    if (!CollectionUtils.isNullOrEmpty(updateTags) && !CollectionUtils.isNullOrEmpty(updateTags.get(DELETE_TAGS_INDEX))) {
                        progress = proxy.initiate("AWS-Redshift-Cluster::DeleteTags", proxyClient, model, callbackContext)
                                .translateToServiceRequest((deleteTagsRequest) -> Translator.translateToDeleteTagsRequest(model, updateTags.get(DELETE_TAGS_INDEX), resourceName))
                                .makeServiceCall(this::deleteTags)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();
                    }

                    if (!CollectionUtils.isNullOrEmpty(updateTags) && !CollectionUtils.isNullOrEmpty(updateTags.get(CREATE_TAGS_INDEX))) {
                        progress = proxy.initiate("AWS-Redshift-Cluster::CreateTags", proxyClient, model, callbackContext)
                                .translateToServiceRequest((createTagsRequest) -> Translator.translateToCreateTagsRequest(model, updateTags.get(CREATE_TAGS_INDEX), resourceName))
                                .makeServiceCall(this::createTags)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();
                    }

                    return progress;
                })

                .then(progress -> {
                    List<List<String>> iamRolesForUpdate = iamRoleUpdate(request.getPreviousResourceState().getIamRoles(), model.getIamRoles());
                    if ((!CollectionUtils.isNullOrEmpty(iamRolesForUpdate)) && (!CollectionUtils.isNullOrEmpty(iamRolesForUpdate.get(ADD_IAM_ROLES_INDEX)) || !CollectionUtils.isNullOrEmpty(iamRolesForUpdate.get(DELETE_IAM_ROLES_INDEX)))) {
                        return proxy.initiate("AWS-Redshift-Cluster::UpdateClusterIAMRoles", proxyClient, model, callbackContext)
                                .translateToServiceRequest((iamRolesModifyRequest) -> Translator.translateToUpdateIAMRolesRequest(model, iamRolesForUpdate))
                                .makeServiceCall(this::updateIAMRoles)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();
                    }
                    return progress;
                })

                .then(progress -> {
                    if (model.getLoggingProperties() == null && isLoggingEnabled(proxyClient, model)) {
                        return proxy.initiate("AWS-Redshift-Cluster::DisableLogging", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToDisableLoggingRequest)
                                .makeServiceCall(this::disableLogging)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();
                    } else if (model.getLoggingProperties() != null && (ObjectUtils.notEqual(model.getLoggingProperties(), request.getPreviousResourceState().getLoggingProperties()))) {
                        return proxy.initiate("AWS-Redshift-Cluster::EnableLogging", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToEnableLoggingRequest)
                                .makeServiceCall(this::enableLogging)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();
                    }
                    return progress;
                })
                .then(progress -> {
                    progress = proxy.initiate("AWS-Redshift-Cluster::DescribeClustersReadOnly", proxyClient, model, callbackContext)
                            .translateToServiceRequest(Translator::translateToDescribeClusterRequest)
                            .makeServiceCall(this::describeCluster)
                            .done((_request, _response, _client, _model, _context) -> {
                                _model = Translator.translateFromReadResponse(_response);
                                model.setDeferMaintenanceIdentifier(_model.getDeferMaintenanceIdentifier());
                                model.setClusterNamespaceArn(_model.getClusterNamespaceArn());
                                return ProgressEvent.progress(model, callbackContext);
                            });
                    return  progress;
                })
                .then(progress -> {
                    if (model.getClusterNamespaceArn() != null && model.getNamespaceResourcePolicy() != null)  {
                        if (model.getNamespaceResourcePolicy().isEmpty()) {
                                return proxy.initiate("AWS-Redshift-Cluster::DeleteNamespaceResourcePolicy", proxyClient, model, callbackContext)
                                        .translateToServiceRequest(Translator::translateToDeleteResourcePolicyRequest)
                                        .makeServiceCall(this::deleteNamespaceResourcePolicy)
                                        .progress();
                        }
                        else {
                            return proxy.initiate("AWS-Redshift-Cluster::PutNamespaceResourcePolicy", proxyClient, model, callbackContext)
                                    .translateToServiceRequest(resourceModel -> Translator.translateToPutResourcePolicy(model, logger))
                                    .makeServiceCall(this::putNamespaceResourcePolicy)
                                    .progress();
                        }
                    }
                    return progress;
                })
                .then(progress -> {
                    if ((ObjectUtils.allNotNull(model.getSnapshotCopyRetentionPeriod()) && issueModifySnapshotCopyRetentionPeriod(request.getPreviousResourceState(), model)) &&
                            isCrossRegionCopyEnabled(proxyClient, model)) {
                        return proxy.initiate("AWS-Redshift-Cluster::ModifySnapshotCopyRetentionPeriod", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToModifySnapshotCopyRetentionPeriodRequest)
                                .makeServiceCall(this::modifySnapshotCopyRetentionPeriod)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();
                    }
                    return progress;
                })

                .then(progress -> {
                    if (model.getDestinationRegion() == null && ObjectUtils.anyNotNull(request.getPreviousResourceState().getDestinationRegion())
                            && isCrossRegionCopyEnabled(proxyClient, model)) {
                        return proxy.initiate("AWS-Redshift-Cluster::DisableSnapshotCopy", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToDisableSnapshotRequest)
                                .makeServiceCall(this::disableSnapshotCopy)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();

                    } else if (model.getDestinationRegion() != null) {
                        if (!isCrossRegionCopyEnabled(proxyClient, model)) {
                            return proxy.initiate("AWS-Redshift-Cluster::EnableSnapshotCopy", proxyClient, model, callbackContext)
                                    .translateToServiceRequest(Translator::translateToEnableSnapshotRequest)
                                    .makeServiceCall(this::enableSnapshotCopy)
                                    .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                    .progress();
                        } else if (isCrossRegionCopyEnabled(proxyClient, model) &&
                                !model.getDestinationRegion().equals(destinationRegionForCrossRegionCopy(proxyClient, model))) {
                            return ProgressEvent.<ResourceModel, CallbackContext>builder()
                                    .status(OperationStatus.FAILED)
                                    .errorCode(HandlerErrorCode.InvalidRequest)
                                    .message(String.format("Snapshot Copy is already enabled on Cluster %s. Invalid Request  %s", model.getClusterIdentifier(),HandlerErrorCode.InvalidRequest.getMessage()))
                                    .build();
                        }
                    }
                    return progress;
                })

                .then(progress -> {
                    if (issueModifyClusterMaintenanceRequest(model)) {
                        return proxy.initiate("AWS-Redshift-Cluster::ModifyClusterMaintenance", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator:: translateToModifyClusterMaintenanceRequest)
                                .makeServiceCall(this::modifyClusterMaintenance)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();
                    }
                    return progress;
                })

                .then(progress -> {
                    if(model.getRevisionTarget() != null && !request.getPreviousResourceState().getRevisionTarget().equals(model.getRevisionTarget())) {
                        return proxy.initiate("AWS-Redshift-Cluster::ModifyClusterDbRevision", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToModifyClusterDbRevisionRequest)
                                .makeServiceCall(this::modifyClusterDbRevision)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterPatched(_client, _model, _context))
                                .done((_request, _response, _client, _model, _context) -> {
                                    if(!callbackContext.getCallbackAfterClusterMaintenance()) {
                                        logger.log(String.format("Update Cluster Db Revision done. %s %s stabilized and available.",ResourceModel.TYPE_NAME, model.getClusterIdentifier()));
                                        callbackContext.setCallbackAfterClusterMaintenance(true);
                                        logger.log ("Initiate a CallBack Delay of "+CALLBACK_DELAY_SECONDS+" seconds after Modify Cluster DbRevision.");
                                        return ProgressEvent.defaultInProgressHandler(callbackContext, CALLBACK_DELAY_SECONDS, _model);
                                    }
                                    return ProgressEvent.progress(_model, callbackContext);
                                });
                    }
                    return progress;
                })

                .then(progress -> {
                    if (model.getAquaConfigurationStatus() != null && !model.getAquaConfigurationStatus().equals(request.getPreviousResourceState().getAquaConfigurationStatus())) {
                        return proxy.initiate("AWS-Redshift-Cluster::ModifyAQUAConfiguration", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator:: translateToModifyAquaConfigurationRequest)
                                .makeServiceCall(this::modifyAquaConfiguration)
                                .stabilize((_request, _response, _client, _model, _context) -> isAquaConfigurationStatusApplied(_client, _model, _context))
                                .done((_request, _response, _client, _model, _context) -> {
                                    if(!callbackContext.getCallbackAfterAquaModify()) {
                                        logger.log(String.format("Update Aqua Configuration done. %s %s stabilized and available.",ResourceModel.TYPE_NAME, model.getClusterIdentifier()));
                                        callbackContext.setCallbackAfterAquaModify(true);
                                        logger.log ("Initiate a CallBack Delay of "+CALLBACK_DELAY_SECONDS+" seconds after Modify Aqua Configuration.");
                                        return ProgressEvent.defaultInProgressHandler(callbackContext, CALLBACK_DELAY_SECONDS, _model);
                                    }
                                    return ProgressEvent.progress(_model, callbackContext);
                                });
                    }
                    return progress;
                })

                .then(progress -> {
                    if (issueResizeClusterRequest(request.getPreviousResourceState(), model)) {
                        return proxy.initiate("AWS-Redshift-Cluster::ResizeCluster", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator:: translateToResizeClusterRequest)
                                .backoffDelay(BACKOFF_STRATEGY)
                                .makeServiceCall(this::resizeCluster)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .done((_request, _response, _client, _model, _context) -> {
                                    logger.log(String.format("Resize Cluster complete. %s %s stabilized and available.",ResourceModel.TYPE_NAME, model.getClusterIdentifier()));
                                    if(!callbackContext.getCallBackAfterResize()) {
                                        callbackContext.setCallBackAfterResize(true);
                                        logger.log ("Initiate a CallBack Delay of "+CALLBACK_DELAY_SECONDS+" seconds after Resize Cluster.");
                                        return ProgressEvent.defaultInProgressHandler(callbackContext, CALLBACK_DELAY_SECONDS, _model);
                                    }
                                    return ProgressEvent.progress(_model, callbackContext);
                                });
                    }
                    return progress;
                })

                .then(progress -> {
                    if (issueModifyClusterParameterGroupRequest(request.getPreviousResourceState(), model)) {
                        return proxy.initiate("AWS-Redshift-Cluster::ModifyClusterParameterGroupName", proxyClient, model, callbackContext)
                                .translateToServiceRequest((modifyClusterRequest) -> Translator.translateToUpdateParameterGroupNameRequest(model, request.getPreviousResourceState()))
                                .makeServiceCall(this::updateCluster)
                                .stabilize((_request, _response, _client, _model, _context) -> stabilizeClusterAfterClusterParameterGroupUpdate(_client, _model, _context))
                                .done((_request, _response, _client, _model, _context) -> {
                                    if(!callbackContext.getCallbackAfterClusterParameterGroupNameModify()) {
                                        logger.log(String.format("Modify Cluster Parameter Group Name done. %s %s stabilized and available.",ResourceModel.TYPE_NAME, model.getClusterIdentifier()));
                                        callbackContext.setCallbackAfterClusterParameterGroupNameModify(true);
                                        logger.log ("Initiate a CallBack Delay of "+CALLBACK_DELAY_SECONDS+" seconds after Modify Cluster Parameter Group Name.");
                                        return ProgressEvent.defaultInProgressHandler(callbackContext, CALLBACK_DELAY_SECONDS, _model);
                                    }
                                    return ProgressEvent.progress(_model, callbackContext);
                                });
                    }
                    return progress;
                })

                .then(progress -> {
                    if ((issueModifyClusterParameterGroupRequest(request.getPreviousResourceState(), model) && isRebootRequired(model, proxyClient)) || isAQUAStatusApplying(model, proxyClient)){
                        return proxy.initiate("AWS-Redshift-Cluster::RebootCluster", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToRebootClusterRequest)
                                .makeServiceCall(this::rebootCluster)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();
                    }
                    return progress;
                })

                .then(progress -> {
                    if (shouldModifyCluster(request.getPreviousResourceState(), model)) {
                        return proxy.initiate("AWS-Redshift-Cluster::UpdateCluster", proxyClient, model, callbackContext)
                                .translateToServiceRequest((modifyClusterRequest) -> Translator.translateToUpdateRequest(model, request.getPreviousResourceState()))
                                .backoffDelay(BACKOFF_STRATEGY)
                                .makeServiceCall(this::updateCluster)
                                .stabilize((_request, _response, _client, _model, _context) -> stabilizeCluster(_client, _model, _context, request))
                                .done((_request, _response, _client, _model, _context) -> {
                                    logger.log(String.format("Modify Cluster complete. %s %s stabilized and available.",ResourceModel.TYPE_NAME, model.getClusterIdentifier()));
                                    if(!callbackContext.getCallBackForReboot()) {
                                        callbackContext.setCallBackForReboot(true);
                                        logger.log ("Initiate a CallBack Delay of "+CALLBACK_DELAY_SECONDS+" seconds after Modify Cluster.");
                                        return ProgressEvent.defaultInProgressHandler(callbackContext, CALLBACK_DELAY_SECONDS, _model);
                                    }
                                    return ProgressEvent.progress(_model, callbackContext);
                                });
                    }
                    return progress;
                })

                .then(progress -> {
                    if(model.getRotateEncryptionKey() != null && model.getRotateEncryptionKey()) {
                        return proxy.initiate("AWS-Redshift-Cluster::RotateEncryptionKey", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToRotateEncryptionKeyRequest)
                                .makeServiceCall(this::rotateEncryptionKey)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();
                    }
                    return progress;
                })

                .then(progress -> {
                    if (model.getResourceAction() != null && PAUSE_CLUSTER.equals(model.getResourceAction())) {
                        return proxy.initiate("AWS-Redshift-Cluster::PauseCluster", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToPauseClusterRequest)
                                .makeServiceCall(this::pauseCluster)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterPaused(_client, _model, _context))
                                .progress();
                    }
                    return progress;
                })

                .then(progress -> {
                    if (model.getResourceAction() != null && FAILOVER_PRIMARY_COMPUTE.equals(model.getResourceAction()) && model.getMultiAZ()) {
                        return proxy.initiate("AWS-Redshift-Cluster::FailoverPrimaryCompute", proxyClient, model, callbackContext)
                                .translateToServiceRequest(Translator::translateToFailoverPrimaryComputeRequest)
                                .makeServiceCall(this::failoverPrimaryComputeCluster)
                                .stabilize((_request, _response, _client, _model, _context) -> isClusterActive(_client, _model, _context))
                                .progress();
                    }
                    return progress;
                })
                .then(progress -> new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger));
        }

    private DescribeClustersResponse describeCluster (
            final DescribeClustersRequest awsRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        DescribeClustersResponse awsResponse = null;
        try {
            logger.log(String.format("%s %s describeClusters.", ResourceModel.TYPE_NAME,
                    awsRequest.clusterIdentifier()));
            awsResponse = proxyClient.injectCredentialsAndInvokeV2(awsRequest, proxyClient.client()::describeClusters);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, awsRequest.clusterIdentifier(), e);
        } catch (final InvalidTagException e) {
            throw new CfnInvalidRequestException(e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }
        logger.log(String.format("%s %s has successfully been read.", ResourceModel.TYPE_NAME, awsRequest.clusterIdentifier()));
        return awsResponse;
    }

    private ModifyClusterResponse updateCluster(
            final ModifyClusterRequest modifyRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        ModifyClusterResponse awsResponse = null;

        try {
            logger.log(String.format("%s %s modifyCluster.", ResourceModel.TYPE_NAME,
                    modifyRequest.clusterIdentifier()));
            awsResponse = proxyClient.injectCredentialsAndInvokeV2(modifyRequest, proxyClient.client()::modifyCluster);
        } catch (final InvalidClusterStateException | InvalidClusterSecurityGroupStateException | UnauthorizedOperationException |
                UnsupportedOptionException | LimitExceededException | InvalidElasticIpException | InvalidClusterTrackException |
                DependentServiceRequestThrottlingException | ClusterSubnetQuotaExceededException | NumberOfNodesQuotaExceededException |
                NumberOfNodesPerClusterLimitExceededException | InsufficientClusterCapacityException | HsmClientCertificateNotFoundException | HsmConfigurationNotFoundException |
                ClusterAlreadyExistsException | TableLimitExceededException | InvalidRetentionPeriodException e ) {
            throw new CfnInvalidRequestException(e);
        } catch (final ClusterNotFoundException | ClusterSecurityGroupNotFoundException |
                ClusterParameterGroupNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, modifyRequest.clusterIdentifier(), e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }

        /*
        After the modify-cluster call, it takes 1 or more seconds for the ACTIVE cluster to be changed to MODIFYING,
        we used to call stabilizer right after the modify-cluster call, and stabilizer immediately returns that the cluster
        is ACTIVE, because the cluster didn't get a chance to be changed to be MODIFYING, which incorrectly tells
        CFN that the modify-cluster is done.

        We wait for 10 seconds just to be on the safe side, it's longer than we need most of the time, but 10 seconds
        is acceptable because the modify-cluster usually takes minutes for the cluster to be ACTIVE again.
         */
        logger.log(String.format("%s %s modify cluster issued, waiting for %s seconds for cluster status to be Modifying",
                ResourceModel.TYPE_NAME,
                modifyRequest.clusterIdentifier(),
                WAIT_TIME_IN_SECS_AFTER_INITIAL_MODIFY_CLUSTER_API_CALL));

        sleep(WAIT_TIME_IN_SECS_AFTER_INITIAL_MODIFY_CLUSTER_API_CALL);

        return awsResponse;
    }

    private ModifyClusterIamRolesResponse updateIAMRoles(
            final ModifyClusterIamRolesRequest modifyRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        ModifyClusterIamRolesResponse awsResponse = null;

        try {
            logger.log(String.format("%s %s modifyClusterIamRoles.", ResourceModel.TYPE_NAME,
                    modifyRequest.clusterIdentifier()));
            awsResponse = proxyClient.injectCredentialsAndInvokeV2(modifyRequest, proxyClient.client()::modifyClusterIamRoles);
        } catch (final InvalidClusterStateException e ) {
            throw new CfnInvalidRequestException(e);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, modifyRequest.clusterIdentifier(), e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }

        logger.log(String.format("%s %s modify IAM Roles issued.", ResourceModel.TYPE_NAME,
                modifyRequest.clusterIdentifier()));

        return awsResponse;
    }

    private ResizeClusterResponse resizeCluster(
            final ResizeClusterRequest resizeClusterRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        ResizeClusterResponse resizeClusterResponse = null;

        try {
            logger.log(String.format("%s %s resizeCluster.", ResourceModel.TYPE_NAME,
                    resizeClusterRequest.clusterIdentifier()));
            resizeClusterResponse = proxyClient.injectCredentialsAndInvokeV2(resizeClusterRequest, proxyClient.client()::resizeCluster);
        } catch (final InvalidClusterStateException | UnauthorizedOperationException |
                UnsupportedOptionException | LimitExceededException | NumberOfNodesQuotaExceededException |
                NumberOfNodesPerClusterLimitExceededException | InsufficientClusterCapacityException |
                UnsupportedOperationException e ) {
            throw new CfnInvalidRequestException(e);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, resizeClusterRequest.clusterIdentifier(), e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }

        logger.log(String.format("%s %s resize cluster issued.", ResourceModel.TYPE_NAME,
                resizeClusterRequest.clusterIdentifier()));

        return resizeClusterResponse;
    }

    private ModifyAquaConfigurationResponse modifyAquaConfiguration(
            final ModifyAquaConfigurationRequest modifyAquaConfigurationRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        ModifyAquaConfigurationResponse modifyAquaConfigurationResponse = null;
        try {
            logger.log(String.format("%s %s modifyAquaConfiguration.", ResourceModel.TYPE_NAME,
                    modifyAquaConfigurationRequest.clusterIdentifier()));
            modifyAquaConfigurationResponse = proxyClient.injectCredentialsAndInvokeV2(modifyAquaConfigurationRequest, proxyClient.client()::modifyAquaConfiguration);
        } catch (final InvalidClusterStateException | UnsupportedOperationException e) {
            throw new CfnInvalidRequestException(e);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, modifyAquaConfigurationRequest.clusterIdentifier(), e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }

        logger.log(String.format("%s %s modifyAquaConfiguration issued.", ResourceModel.TYPE_NAME,
                modifyAquaConfigurationRequest.clusterIdentifier()));

        return modifyAquaConfigurationResponse;
    }

    private ModifyClusterMaintenanceResponse modifyClusterMaintenance(
            final ModifyClusterMaintenanceRequest modifyClusterMaintenanceRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        ModifyClusterMaintenanceResponse modifyClusterMaintenanceResponse = null;

        try {
            logger.log(String.format("%s %s modifyClusterMaintenance.", ResourceModel.TYPE_NAME,
                    modifyClusterMaintenanceRequest.clusterIdentifier()));
            modifyClusterMaintenanceResponse = proxyClient.injectCredentialsAndInvokeV2(modifyClusterMaintenanceRequest, proxyClient.client()::modifyClusterMaintenance);
        } catch (final InvalidClusterStateException e ) {
            throw new CfnInvalidRequestException(modifyClusterMaintenanceRequest.toString(), e);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, modifyClusterMaintenanceRequest.clusterIdentifier());
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(modifyClusterMaintenanceRequest.toString(), e);
        }

        logger.log(String.format("%s %s modifyClusterMaintenance issued.", ResourceModel.TYPE_NAME,
                modifyClusterMaintenanceRequest.clusterIdentifier()));

        return modifyClusterMaintenanceResponse;
    }

    private ModifyClusterDbRevisionResponse modifyClusterDbRevision(
            final ModifyClusterDbRevisionRequest modifyClusterDbRevisionRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        ModifyClusterDbRevisionResponse modifyClusterDbRevisionResponse = null;

        try {
            logger.log(String.format("%s %s modifyClusterDbRevisionRequest.", ResourceModel.TYPE_NAME,
                    modifyClusterDbRevisionRequest.clusterIdentifier()));
            modifyClusterDbRevisionResponse = proxyClient.injectCredentialsAndInvokeV2(modifyClusterDbRevisionRequest, proxyClient.client()::modifyClusterDbRevision);
        } catch (final InvalidClusterStateException | ClusterOnLatestRevisionException e ) {
            throw new CfnInvalidRequestException(modifyClusterDbRevisionRequest.toString(), e);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, modifyClusterDbRevisionRequest.clusterIdentifier());
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(modifyClusterDbRevisionRequest.toString(), e);
        }

        logger.log(String.format("%s %s modifyClusterDbRevisionRequest issued.", ResourceModel.TYPE_NAME,
                modifyClusterDbRevisionRequest.clusterIdentifier()));

        return modifyClusterDbRevisionResponse;
    }

    private CreateTagsResponse createTags(
            final CreateTagsRequest createTagsRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        CreateTagsResponse createTagsResponse = null;

        try {
            logger.log(String.format("createTags for %s", createTagsRequest.resourceName()));
            createTagsResponse = proxyClient.injectCredentialsAndInvokeV2(createTagsRequest, proxyClient.client()::createTags);
        } catch (final InvalidClusterStateException | TagLimitExceededException | InvalidTagException e ) {
            throw new CfnInvalidRequestException(e);
        } catch (final ResourceNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, createTagsRequest.resourceName(), e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }

        logger.log(String.format("%s %s create tags for resource issued.", ResourceModel.TYPE_NAME,
                createTagsRequest.resourceName()));

        return createTagsResponse;
    }

    private DeleteTagsResponse deleteTags(
            final DeleteTagsRequest deleteTagsRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        DeleteTagsResponse deleteTagsResponse = null;

        try {
            logger.log(String.format("deleteTags for %s", deleteTagsRequest.resourceName()));
            deleteTagsResponse = proxyClient.injectCredentialsAndInvokeV2(deleteTagsRequest, proxyClient.client()::deleteTags);
        } catch (final InvalidClusterStateException | TagLimitExceededException | InvalidTagException e ) {
            throw new CfnInvalidRequestException(e);
        } catch (final ResourceNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, deleteTagsRequest.resourceName(), e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }

        logger.log(String.format("%s %s delete tags for resource issued.", ResourceModel.TYPE_NAME,
                deleteTagsRequest.resourceName()));

        return deleteTagsResponse;
    }

    private DisableLoggingResponse disableLogging(
            final DisableLoggingRequest disableLoggingRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        DisableLoggingResponse disableLoggingResponse = null;

        try {
            logger.log(String.format("%s %s disableLogging.", ResourceModel.TYPE_NAME,
                    disableLoggingRequest.clusterIdentifier()));
            disableLoggingResponse = proxyClient.injectCredentialsAndInvokeV2(disableLoggingRequest, proxyClient.client()::disableLogging);
        } catch (final ClusterNotFoundException  e) {
            throw new CfnInvalidRequestException(e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }
        logger.log(String.format("%s %s disable logging properties issued.", ResourceModel.TYPE_NAME,
                disableLoggingRequest.clusterIdentifier()));

        return disableLoggingResponse;
    }

    private EnableLoggingResponse enableLogging(
            final EnableLoggingRequest enableLoggingRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        EnableLoggingResponse enableLoggingResponse = null;

        try {
            logger.log(String.format("%s %s enableLogging.", ResourceModel.TYPE_NAME,
                    enableLoggingRequest.clusterIdentifier()));
            enableLoggingResponse = proxyClient.injectCredentialsAndInvokeV2(enableLoggingRequest, proxyClient.client()::enableLogging);
        } catch (final ClusterNotFoundException | BucketNotFoundException | InsufficientS3BucketPolicyException
                | InvalidS3KeyPrefixException | InvalidS3BucketNameException | InvalidClusterStateException  e) {
            throw new CfnInvalidRequestException(e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }
        logger.log(String.format("%s %s enable logging properties issued.",
                ResourceModel.TYPE_NAME, enableLoggingRequest.clusterIdentifier()));

        return enableLoggingResponse;
    }

    private EnableSnapshotCopyResponse enableSnapshotCopy(
            final EnableSnapshotCopyRequest enableSnapshotCopyRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        EnableSnapshotCopyResponse enableSnapshotCopyResponse = null;
        try {
            enableSnapshotCopyResponse = proxyClient.injectCredentialsAndInvokeV2(enableSnapshotCopyRequest, proxyClient.client()::enableSnapshotCopy);
        } catch (final InvalidClusterStateException | IncompatibleOrderableOptionsException | CopyToRegionDisabledException
                | SnapshotCopyAlreadyEnabledException | UnknownSnapshotCopyRegionException | UnauthorizedOperationException |
                SnapshotCopyGrantNotFoundException | LimitExceededException | DependentServiceRequestThrottlingException
                | InvalidRetentionPeriodException e ) {
            throw new CfnInvalidRequestException(e);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, enableSnapshotCopyRequest.clusterIdentifier());
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }

        logger.log(String.format("Enable Cluster Snapshot Copy issued for %s %s in destination region %s.", ResourceModel.TYPE_NAME,
                enableSnapshotCopyRequest.clusterIdentifier(), enableSnapshotCopyRequest.destinationRegion()));

        return enableSnapshotCopyResponse;
    }

    private DisableSnapshotCopyResponse disableSnapshotCopy(
            final DisableSnapshotCopyRequest disableSnapshotCopyRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        DisableSnapshotCopyResponse disableSnapshotCopyResponse = null;

        try {
            disableSnapshotCopyResponse = proxyClient.injectCredentialsAndInvokeV2(disableSnapshotCopyRequest, proxyClient.client()::disableSnapshotCopy);
        } catch (final InvalidClusterStateException | SnapshotCopyAlreadyDisabledException | UnauthorizedOperationException e ) {
            throw new CfnInvalidRequestException(disableSnapshotCopyRequest.toString(), e);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, disableSnapshotCopyRequest.clusterIdentifier());
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(disableSnapshotCopyRequest.toString(), e);
        }

        logger.log(String.format("Disable Cluster Snapshot Copy issued for %s %s .", ResourceModel.TYPE_NAME,
                disableSnapshotCopyRequest.clusterIdentifier()));

        return disableSnapshotCopyResponse;
    }

    private ModifySnapshotCopyRetentionPeriodResponse modifySnapshotCopyRetentionPeriod(
            final ModifySnapshotCopyRetentionPeriodRequest modifySnapshotCopyRetentionPeriodRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        ModifySnapshotCopyRetentionPeriodResponse modifySnapshotCopyRetentionPeriodResponse = null;

        try {
            modifySnapshotCopyRetentionPeriodResponse = proxyClient.injectCredentialsAndInvokeV2(modifySnapshotCopyRetentionPeriodRequest, proxyClient.client()::modifySnapshotCopyRetentionPeriod);
        } catch (final InvalidClusterStateException | SnapshotCopyDisabledException | UnauthorizedOperationException | InvalidRetentionPeriodException e ) {
            throw new CfnInvalidRequestException(modifySnapshotCopyRetentionPeriodRequest.toString(), e);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, modifySnapshotCopyRetentionPeriodRequest.clusterIdentifier());
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(modifySnapshotCopyRetentionPeriodRequest.toString(), e);
        }

        logger.log(String.format("Modify Snapshot Copy Retention Period issued for %s %s .", ResourceModel.TYPE_NAME,
                modifySnapshotCopyRetentionPeriodRequest.clusterIdentifier()));

        return modifySnapshotCopyRetentionPeriodResponse;
    }

    private RebootClusterResponse rebootCluster (
            final RebootClusterRequest rebootClusterRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        RebootClusterResponse rebootClusterResponse = null;
        try {
            logger.log(String.format("%s %s rebootCluster.", ResourceModel.TYPE_NAME,
                    rebootClusterRequest.clusterIdentifier()));
            rebootClusterResponse = proxyClient.injectCredentialsAndInvokeV2(rebootClusterRequest, proxyClient.client()::rebootCluster);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, rebootClusterRequest.clusterIdentifier(), e);
        } catch (final InvalidClusterStateException e) {
            throw new CfnInvalidRequestException(e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }

        logger.log(String.format("%s %s Reboot Cluster issued.", ResourceModel.TYPE_NAME,
                rebootClusterRequest.clusterIdentifier()));
        return rebootClusterResponse;
    }

    private ResumeClusterResponse resumeCluster (
            final ResumeClusterRequest resumeClusterRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        ResumeClusterResponse resumeClusterResponse = null;
        try {
            logger.log(String.format("%s %s resumeCluster.", ResourceModel.TYPE_NAME,
                    resumeClusterRequest.clusterIdentifier()));
            resumeClusterResponse = proxyClient.injectCredentialsAndInvokeV2(resumeClusterRequest, proxyClient.client()::resumeCluster);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, resumeClusterRequest.clusterIdentifier(), e);
        } catch (final InvalidClusterStateException | InsufficientClusterCapacityException e) {
            throw new CfnInvalidRequestException(e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }

        logger.log(String.format("%s %s Resume Cluster issued.", ResourceModel.TYPE_NAME,
                resumeClusterRequest.clusterIdentifier()));
        return resumeClusterResponse;
    }

    private PauseClusterResponse pauseCluster (
            final PauseClusterRequest pauseClusterRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        PauseClusterResponse pauseClusterResponse = null;
        try {
            logger.log(String.format("%s %s pauseCluster.", ResourceModel.TYPE_NAME,
                    pauseClusterRequest.clusterIdentifier()));
            pauseClusterResponse = proxyClient.injectCredentialsAndInvokeV2(pauseClusterRequest, proxyClient.client()::pauseCluster);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, pauseClusterRequest.clusterIdentifier(), e);
        } catch (final InvalidClusterStateException e) {
            throw new CfnInvalidRequestException(e);
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(e);
        }

        logger.log(String.format("%s %s Pause Cluster issued.", ResourceModel.TYPE_NAME,
                pauseClusterRequest.clusterIdentifier()));
        return pauseClusterResponse;
    }

    private RotateEncryptionKeyResponse rotateEncryptionKey(
            final RotateEncryptionKeyRequest rotateEncryptionKeyRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        RotateEncryptionKeyResponse rotateEncryptionKeyResponse = null;

        try {
            logger.log(String.format("%s %s RotateEncryptionKey.", ResourceModel.TYPE_NAME,
                    rotateEncryptionKeyRequest.clusterIdentifier()));
            rotateEncryptionKeyResponse = proxyClient.injectCredentialsAndInvokeV2(rotateEncryptionKeyRequest, proxyClient.client()::rotateEncryptionKey);
        } catch (final InvalidClusterStateException | DependentServiceRequestThrottlingException e ) {
            throw new CfnInvalidRequestException(rotateEncryptionKeyRequest.toString(), e);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, rotateEncryptionKeyRequest.clusterIdentifier());
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(rotateEncryptionKeyRequest.toString(), e);
        }

        logger.log(String.format("%s %s RotateEncryptionKey issued.", ResourceModel.TYPE_NAME,
                rotateEncryptionKeyRequest.clusterIdentifier()));

        return rotateEncryptionKeyResponse;
    }

    private PutResourcePolicyResponse putNamespaceResourcePolicy(
            final PutResourcePolicyRequest putRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        PutResourcePolicyResponse putResponse = null;

        try {
            putResponse = proxyClient.injectCredentialsAndInvokeV2(putRequest, proxyClient.client()::putResourcePolicy);
        } catch (ResourceNotFoundException e){
            throw new CfnNotFoundException(e);
        } catch (InvalidPolicyException | UnsupportedOperationException | InvalidParameterValueException e) {
            throw new CfnInvalidRequestException(ResourceModel.TYPE_NAME, e);
        } catch (SdkClientException | RedshiftException  e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s successfully put resource policy.", ResourceModel.TYPE_NAME));
        return putResponse;
    }

    private DeleteResourcePolicyResponse deleteNamespaceResourcePolicy(final DeleteResourcePolicyRequest deleteRequest,
                                                                       final ProxyClient<RedshiftClient> proxyClient) {
        DeleteResourcePolicyResponse deleteResponse = null;
        try{
            deleteResponse = proxyClient.injectCredentialsAndInvokeV2(deleteRequest, proxyClient.client()::deleteResourcePolicy);
        } catch (ResourceNotFoundException e){
            throw new CfnNotFoundException(e);
        } catch ( UnsupportedOperationException e) {
            throw new CfnInvalidRequestException(ResourceModel.TYPE_NAME, e);
        } catch (SdkClientException | RedshiftException  e) {
            throw new CfnGeneralServiceException(ResourceModel.TYPE_NAME, e);
        }

        logger.log(String.format("%s successfully deleted resource policy.", ResourceModel.TYPE_NAME));
        return deleteResponse;
    }

    private FailoverPrimaryComputeResponse failoverPrimaryComputeCluster(
            final FailoverPrimaryComputeRequest failoverPrimaryComputeRequest,
            final ProxyClient<RedshiftClient> proxyClient) {
        FailoverPrimaryComputeResponse failoverPrimaryComputeResponse = null;

        try {
            logger.log(String.format("%s %s FailoverPrimaryCompute.", ResourceModel.TYPE_NAME,
                    failoverPrimaryComputeRequest.clusterIdentifier()));
            failoverPrimaryComputeResponse = proxyClient.injectCredentialsAndInvokeV2(failoverPrimaryComputeRequest, proxyClient.client()::failoverPrimaryCompute);
        } catch (final InvalidClusterStateException | DependentServiceRequestThrottlingException |
                       UnauthorizedOperationException | UnsupportedOperationException e ) {
            throw new CfnInvalidRequestException(failoverPrimaryComputeRequest.toString(), e);
        } catch (final ClusterNotFoundException e) {
            throw new CfnNotFoundException(ResourceModel.TYPE_NAME, failoverPrimaryComputeRequest.clusterIdentifier());
        } catch (SdkClientException | AwsServiceException e) {
            throw new CfnGeneralServiceException(failoverPrimaryComputeRequest.toString(), e);
        }

        logger.log(String.format("%s %s failoverPrimaryCompute issued.", ResourceModel.TYPE_NAME,
                failoverPrimaryComputeRequest.clusterIdentifier()));

        return failoverPrimaryComputeResponse;
    }

    /*
    We used to compare the values like this:
    notEqual(oldModel.getAllowVersionUpgrade(), newModel.getAllowVersionUpgrade()) ||
    notEqual(... more attributes)

    The above is type-safe, but when we need to provide detailed logs about which attributes changed,
    adding logs for each attribute is tedious (I couldn't figure out how, feel free to PR if you know how).

    The approach below uses Reflection APIs to get the attribute dynamically,
    which is not 100% type-safe, but unit tests are ensuring every hard-coded attributes
    in the list (and their getter methods) exist on the ResourceModel (generated from aws-redshift-cluster.json).

    Pros of the approach below:
    - update attributes list (whether its change triggers modifyCluster) easily.
    - reduce duplicated code

    As always, never log anything sensitive :)
     */
    private boolean shouldModifyCluster(ResourceModel oldModel, ResourceModel newModel) {
        // any 1 or more attribute value change regardless of sensitive/insensitive,
        // will trigger a modifyClusterRequest
        // get combined attributes (insensitive + sensitive)
        final String[] allAttributes = Stream
                .concat(
                        Arrays.stream(DETECTABLE_MODIFY_CLUSTER_ATTRIBUTES_INSENSITIVE),
                        Arrays.stream(DETECTABLE_MODIFY_CLUSTER_ATTRIBUTES_SENSITIVE)
                ).toArray(String[]::new);

        boolean shouldModifyCluster = false;

        logger.log("Checking cluster attribute values changes for ModifyCluster...");

        // for loop to log every attribute's value change for debugging
        for (String attribute : allAttributes) {
            final Object oldModelValue = getAttributeValue(oldModel, attribute);
            final Object newModelValue = getAttributeValue(newModel, attribute);

            boolean attributeValueChanged = ObjectUtils.notEqual(oldModelValue, newModelValue);

            // if an attribute changed, we log both values,
            // i.e. "PubliclyAccessible change from true to false"
            if (attributeValueChanged) {
                if (Arrays.asList(DETECTABLE_MODIFY_CLUSTER_ATTRIBUTES_SENSITIVE).contains(attribute)) {
                    // Be CAREFUL, we don't log any sensitive attribute values
                    logger.log(String.format("Sensitive attribute %s changed", attribute));
                } else {
                    // insensitive attributes
                    logger.log(String.format("%s changed from %s to %s", attribute, oldModelValue, newModelValue));
                }
            }

            shouldModifyCluster = shouldModifyCluster || attributeValueChanged;
        }

        if (shouldModifyCluster) {
            logger.log("Cluster attribute(s) changes detected, should issue modifyClusterRequest");
        } else {
            logger.log("No cluster attribute changes detected, should skip modifyClusterRequest");
        }

        return shouldModifyCluster;
    }

    private Object getAttributeValue(ResourceModel model, String attribute) {
        try {
            Method getter = ResourceModel.class.getMethod("get" + attribute);
            return getter.invoke(model);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.log(e.getStackTrace().toString());
            // This should never get triggered during runtime.
            //
            // we throw RuntimeException here to ensure unit test catches any attribute typos,
            // for example, if developers mistype an attribute in the list,
            // unit test would throw the following exception and fail,
            // which stops build process.
            //
            // Only if the all attributes match the Model generated from aws-redshift-cluster.json,
            // the tests will pass
            throw new RuntimeException(String.format("Failed to get %s from cluster to decide whether to modifyCluster", attribute));
        }
    }
}
