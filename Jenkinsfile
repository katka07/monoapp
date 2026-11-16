#!/usr/bin/env groovy

// 1. Helper for multiple credentials
// Uses string() for single values (URL, Host) and usernamePassword() for pairs

def DOCKER_REGISTRY = 'katka07'

def withSecrets(Closure body) {
  withCredentials([
    string(credentialsId: 'JFROG_URL', variable: 'JFROG_URL'),
    string(credentialsId: 'DB_HOST',   variable: 'DB_HOST'),
    
    usernamePassword(credentialsId: 'jfrog-creds',
                     usernameVariable: 'JFROG_USER',
                     passwordVariable: 'JFROG_PASS'),
                     
    usernamePassword(credentialsId: 'db-creds',
                     usernameVariable: 'DB_USER',
                     passwordVariable: 'DB_PASS')
  ]) {
    body()
  }
}

pipeline {
  agent {
    docker {
      label 'work-agent'
      // Use the global environment variable for the image
      image "${DOCKER_REGISTRY}/monolith:v1" 
      // Correct key for Docker Hub authentication
      registryCredentialsId 'docker_hub_token'
      alwaysPull true
      args '--user root'
    }
  }

  environment {
    // Define the registry once
    DOCKER_REGISTRY = 'katka07'
    // Resolve the full image name for use in stages if needed
    CI_RUNNER_IMAGE = "${DOCKER_REGISTRY}/monolithi:v1"
  }

  options {
    disableConcurrentBuilds()
    timestamps()
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Set Build Version') {
      steps {
        withSecrets {
          sh "make set-version BUILD_NUMBER=${env.BUILD_NUMBER}"
        }
      }
    }

    stage('Unit Tests') {
      steps {
        withSecrets {
          sh 'make test-unit'
        }
      }
    }

    stage('Integration Tests') {
      steps {
        withSecrets {
          sh 'make test-integration'
        }
      }
    }

    stage('E2E Tests') {
      steps {
        withSecrets {
          sh 'make test-e2e'
        }
      }
    }

    stage('Publish Build To JFrog') {
      steps {
        withSecrets {
          sh "make publish-candidate BUILD_NUMBER=${env.BUILD_NUMBER}"
        }
      }
    }

    stage('Deploy To DEV Env') {
      steps {
        withSecrets {
          sh "make deploy-dev BUILD_NUMBER=${env.BUILD_NUMBER}"
        }
      }
    }

    stage('Smoke/Validate DEV Env') {
      steps {
        withSecrets {
          sh "make smoke-dev BUILD_NUMBER=${env.BUILD_NUMBER}"
        }
      }
    }

    stage('Promote to PROD') {
      steps {
        input message: 'Promote to Prod?', ok: 'Deploy to Prod!'
      }
    }

    stage('Promote Artifact To Prod') {
      steps {
        withSecrets {
          sh "make promote-prod BUILD_NUMBER=${env.BUILD_NUMBER}"
        }
      }
    }

    stage('Deploy To Prod Env') {
      steps {
        withSecrets {
          sh "make deploy-prod BUILD_NUMBER=${env.BUILD_NUMBER}"
        }
      }
    }

    stage('Smoke/Validate PROD Env') {
      steps {
        withSecrets {
          sh "make smoke-prod BUILD_NUMBER=${env.BUILD_NUMBER}"
        }
      }
    }
  }
}
