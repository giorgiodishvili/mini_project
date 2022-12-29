package com.newagesol.mini_proj.configuration;

import java.util.Map;

public record SQSConfigProperties(
        Map<String, String> urls,
        Integer batchSize,
        Integer pollWaitTimeSec,
        Boolean parallelProcessing
) {

}
