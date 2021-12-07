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
                    docker rmi -f slackbot
                    docker build -t slackbot .
                '''
            }
        }
        stage("Run") {
            steps {
                sh '''
                    docker run -it \
                    -- rm \
                    --name slackbot
                    slackbot
                '''
            }
        }
    }
}
