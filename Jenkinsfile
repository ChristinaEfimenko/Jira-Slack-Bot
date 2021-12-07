pipeline {
    agent {
        label 'slackBot_onHold_runner'
    }
    options {
        disableConcurrentBuilds()
    }
    stages {
        stage("Build") {
            steps {
                sh '''
                    docker rmi -f slackBot
                    docker build -t slackBot .
                '''
            }
        }
        stage("Run") {
            steps {
                sh '''
                    docker run -it \
                    -- rm \
                    --name slackBot
                    slackBot
                '''
            }
        }
    }
}
