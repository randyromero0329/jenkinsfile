pipeline {
    agent any 
   /* {
        label 'jenkins-paynamics'
    }
    environment {
        servIP = "35.163.200.179"
        commit = '"Update Config"'
    } */
    stages { 
        stage('Push Notification: Start') {
           steps {
             echo '=========== Notify on Telegram Bot : Build started ============' 
              script{
                withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                string(credentialsId: 'telegramChatID', variable: 'CHAT_ID')]) {
                    sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                    -d chat_id=${CHAT_ID} \
                    -d parse_mode="HTML" \
                    -d text="<b>Build started on wireguard-paynamics</b>"'
                    }
                }
            }
        }
        stage('Clone Repo'){
            steps {
                echo '=========== Cloning ENV Repository ============'
              /*  script {
                    sh 'ssh ubuntu@${servIP} "git clone git@gitlab.paynamics.net:devops/wireguard-paynamics.git; \
                    cd wireguard-paynamics \
                    && git checkout master \
                    && git pull origin master"'
                } */
            }
        }  
        stage ('Push Notification: Approval') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                        string(credentialsId: 'telegramChatID', variable: 'CHAT_ID')]) {
                        sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                        -d chat_id=${CHAT_ID} \
                        -d parse_mode="HTML" \
                        -d text="<b>Paused! Waiting the approval for wireguard-paynamics</b>"'
                    }
                }
            }
        }
        stage ('Deployment Approval') {
            steps {
                script {
                    def proceed = true
                    try {
                        timeout(time: 300, unit: 'SECONDS') {
                            input('Do you want to proceed for production deployment?')
                        }
                        echo 'test try'
                        sh 'echo "Deployment to production will resume"'
                    } catch (err) {
                        proceed = false
                        echo 'test catch'
                        currentBuild.result = 'ABORTED'
                    }
                    if(proceed) {
                        echo 'test true'
                        withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                        string(credentialsId: 'telegramChatID', variable: 'CHAT_ID')]) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>Build approved! Build will resume for wireguard-paynamics</b>"'
                        }
                    } else {
                        echo 'test false'
                        withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                        string(credentialsId: 'telegramChatID', variable: 'CHAT_ID')]) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>Build aborted! Approval is needed for wireguard-paynamics</b>"'
                        }
                        currentBuild.result = 'ABORTED'
                        error('Stopping early…')
                    }
                }
            } 
        }       
        stage('Build and Run'){
            steps {
                echo '=========== Building and Running Docker Container ============'
             /*   script {
                   try {
                        sh 'ssh ubuntu@${servIP} "cd wireguard-paynamics \
                        && docker stop wireguard \
                        && docker rm wireguard \
                        && docker-compose up -d wireguard"'
                    } catch (Exception e) {
                        withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                        string(credentialsId: 'telegramChatID', variable: 'CHAT_ID')]) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>Notice! Build and Run was failed on wireguard-paynamics</b>"'
                        }
                        currentBuild.result = 'ABORTED'
                        error('Stopping early…')
                    }
                } */
            }
        }
  /*      stage('Push Config File'){
            steps {
                echo '=========== Push to Repository ============'
                script {
                    sh 'ssh ubuntu@${servIP} "cd wireguard-paynamics; \
                    git add . \
                    && git commit -m ${commit} \
                    && git push origin master-config \
                    && git fetch --all \
                    && git reset --hard origin/master"'
                }
            }
        } */
        stage('Push Notification: Finished') {
            steps {
                echo '=========== Notify on Telegram Bot : Build successful ============'
                script{
                    withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                    string(credentialsId: 'telegramChatID', variable: 'CHAT_ID')]) {
                        sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                        -d chat_id=${CHAT_ID} \
                        -d parse_mode="HTML" \
                        -d text="<b>Congratulations! Build successful for wireguard-paynamics</b>"'
                    }
                }
            }
        }         
    }
}
