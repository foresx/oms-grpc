#!/usr/bin/env groovy

properties([
  buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '15', numToKeepStr: '')),
  pipelineTriggers([[$class: 'PeriodicFolderTrigger', interval: '1d']]),
])

node {
  catchError {
    timeout(time: 30, unit: 'MINUTES') {
      env.APPLICATION_NAME = "oms"
      env.ECR_REGISTRY = "825303761615.dkr.ecr.ap-southeast-1.amazonaws.com"

      stage('Clone repository') {
        def scmVars = checkout([
          $class: 'GitSCM',
          branches: scm.branches,
          extensions: scm.extensions + [
            [$class: 'CloneOption', honorRefspec: true, noTags: false],
            [$class: 'SubmoduleOption', disableSubmodules: false, parentCredentials: true],
            [$class: 'CleanCheckout']
          ],
          userRemoteConfigs: scm.userRemoteConfigs
        ])
      }
      env.APPLICATION_VERSION = sh(returnStdout: true, script: 'git describe --always --abbrev=8 HEAD').trim()
      env.SECOND_EPOCH = sh(returnStdout: true, script: 'date +%s').trim()
      env.IMAGE_TAG = "${APPLICATION_VERSION}-b${SECOND_EPOCH}"
      env.APPLICATION_COMMIT = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
      echo "Building docker image using image tag: ${IMAGE_TAG}"

      stage('Build Jar') {
        withCredentials([string(credentialsId: 'vault_role_id', variable: 'ROLE_ID'),
                            string(credentialsId: 'vault_jenkins_token', variable: 'VAULT_TOKEN'),
                            string(credentialsId: 'azureArtifactsGradleAccessToken', variable: 'AZURE_ARTIFACTS_ENV_ACCESS_TOKEN')]) {
          sshagent (['user-app-keeper-ssh-key']) {
              sh '''
              set +x
              ./scripts/build-java.sh
              '''
          }
        }
      }

      if(env.TAG_NAME){
        build_app_image('prod')
      } else {
        branchName = env.BRANCH_NAME
        switch (branchName) {
          case ~/^master$/:
              build_app_image('test')
              build_app_image('prod')
              break
          case ~/^develop$/:
              build_app_image('dev')
              build_app_image('test')
              break
          case ~/^build.*$/:
              build_app_image('dev')
              build_app_image('test')
              break
          default:
              echo "Unsupported branch name: ${branchName}"
        }
      }
    }
  }
  // notifyBuild(currentBuild.result)
}

def build_app_image(String application_env) {
  application_env = application_env ?: ''
  if (application_env == ''){
    echo "Undefined application_env: ${application_env}"
    return
  }
  withEnv(["APPLICATION_ENV=${application_env}"]) {
    stage("Build image: ${APPLICATION_NAME}:${APPLICATION_ENV}") {
      withCredentials([string(credentialsId: 'vault_role_id', variable: 'ROLE_ID'),
                            string(credentialsId: 'vault_jenkins_token', variable: 'VAULT_TOKEN'),
                            string(credentialsId: 'azureArtifactsGradleAccessToken', variable: 'AZURE_ARTIFACTS_ENV_ACCESS_TOKEN')]) {
        sshagent (['user-app-keeper-ssh-key']) {
            sh '''
            set +x
            export VAULT_ADDR=http://vault.castlery.internal:8200
            export VAULT_SKIP_VERIFY=true
            vault token-renew
            export SECRET_ID=$(vault write -field=secret_id -f auth/approle/role/app-role/secret-id)
            export VAULT_TOKEN=$(vault write -field=token auth/approle/login role_id=${ROLE_ID} secret_id=${SECRET_ID})
            ./scripts/build-docker.sh ${APPLICATION_ENV} ${APPLICATION_VERSION} ${IMAGE_TAG}
            '''
        }
      }

    }
  }
}

def publish_image(String module, String application_env) {
  withEnv(["APPLICATION_NAME=${module}", "APPLICATION_ENV=${application_env}"]) {
    image = docker.image("${APPLICATION_NAME}:${APPLICATION_ENV}_${IMAGE_TAG}")
    // push to ECR
    withCredentials([string(credentialsId: 'app_keeper_aws_access_key_id', variable: 'AWS_ACCESS_KEY_ID'),
                    string(credentialsId: 'app_keeper_aws_secret_access_key', variable: 'AWS_SECRET_ACCESS_KEY')]) {
      sh '''
      docker tag "${APPLICATION_NAME}:${APPLICATION_ENV}_${IMAGE_TAG}" "${ECR_REGISTRY}/${APPLICATION_NAME}:${APPLICATION_ENV}_${IMAGE_TAG}"
      docker push "${ECR_REGISTRY}/${APPLICATION_NAME}:${APPLICATION_ENV}_${IMAGE_TAG}"
      '''
    }
  }
}

def notifyBuild(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus = buildStatus ?: 'SUCCESS'
  gitCommit = sh(returnStdout: true, script: 'git describe --always --abbrev=8 HEAD').trim()
  commitChangeset = sh(returnStdout: true, script: 'git log --oneline -1').trim()
  // Default values
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = """- Job     : ${env.JOB_NAME}
                  |- Version : ${gitCommit}
                  |- Commit  : ${commitChangeset}""".stripMargin()
  withCredentials([string(credentialsId: 'office365-jenkins-webhook-url', variable: 'WEBHOOK_URL')]) {
    office365ConnectorSend (message: summary, status: buildStatus, webhookUrl: "${env.WEBHOOK_URL}")
  }

  def recipient = "team.tech@castlery.com"
  mail (subject: subject,
          body: """Job: ${env.JOB_NAME}
                  |Build: ${env.BUILD_NUMBER}
                  |Status: ${buildStatus}
                  |Build URL: ${env.BUILD_URL}
                """.stripMargin(),
            to: recipient,
      replyTo: recipient,
          from: 'Jenkins <jenkins.noreply@castlery.com>')
}
