pipeline {
    agent {
        label 'test_executor_1'
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
                    docker run \                
                    --name slackbot \
                    slackbot
                '''
            }
        }
    }
}
