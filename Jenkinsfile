pipeline {
    agent {
        label 'slackBot_onHold'
    }
    options {
        disableConcurrentBuilds()
    }
    stages {
        stage("Build") {
            steps {
                sh '''
                    docker rm -f slackbot
                    docker rmi -f slackbot
                    docker build -t slackbot .
                '''
            }
        }
        stage("Run") {
            steps {
                sh '''
                    docker run --name slackbot slackbot                     
                '''
            }
        }
    }
}
