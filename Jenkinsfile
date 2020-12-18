Map pipelineParams = [
    "slackChannel"                          : "data-weave-bot",
    "enableSlackSuccessNotifications"        : true,
    "enableSlackFailedTestsNotifications"    : true,
    "enableAllureTestReportStage"           : false,
    "enableSonarQubeStage"                  : false,
    "enableNexusIqStage"                    : false,
    "mavenSettingsXmlId"                    : "data-weave-maven-settings",
    "projectType"                           : "Shell",
    "devBranchesRegex"                      : "master",
    "enableScheduleTrigger"                 : true,
    "scheduleTriggerCommand"                : "@daily",
    "junitTestResults"                      : '**/build/test-results/test/*.xml, **/build/test-results/*.xml',
    "upstreamProjects": "DataWeave/data-weave/master"
]

runtimeBuild(pipelineParams)
