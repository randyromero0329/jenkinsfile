pipeline {
    agent any
    environment {
        uat_bis = 'UAT-bis'
        uat_cde = 'UAT-cde'
        uat_ebiller = 'UAT-ebiller'
        uat_fileserver = 'UAT-fileserver'
        uat_gateway = 'UAT-gateway'
        uat_ncde = 'UAT-ncde'
        uat_PaygateHVM = 'UAT-PaygateHVM'
        uat_ptiapps = 'UAT-ptiapps'
        uat_ptipaygate = 'UAT-ptipaygate'
        uat_recon = 'UAT-recon'
        uat_webterminal = 'UAT-webterminal'

        // Instance IDs to be rebooted
       // id_uat_bis = 'i-07b575a118ff5b1a6'
       // id_uat_cde = 'i-073b53d62cae3aefe'
       // id_uat_ebiller = 'i-0374825351e79bba3'
       // id_uat_fileserver = 'i-05803b05bf4fc44d8'
       // id_uat_gateway = 'i-0a94792077a36c8cd'
       // id_uat_ncde = 'i-008ae1dfaecfdf662'
       // id_uat_PaygateHVM = 'i-0b5e0a696afd25156'
       // id_uat_ptiapps = 'i-0b0dfdd46256b341e'
       // id_uat_ptipaygate = 'i-08226d3452e70e8ac'
       // id_uat_recon = 'i-04e5f8656c0c834ee'
       // id_uat_webterminal = 'i-0a30b880bfc8e1126'
    }
    stages {
        stage('Push Notification: Start') {
            steps {
                echo '=========== Notification: Reboot Started ============'
                withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                string(credentialsId: 'telegramChatID', variable: 'CHAT_ID')]) {
                    sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                    -d chat_id=${CHAT_ID} \
                    -d parse_mode="HTML" \
                    -d text="<strong>Notice!, UAT Servers Reboot started! \n\n Affected Servers: </strong> <i> \n 👉 ${uat_bis} \n 👉 ${uat_cde} \n 👉 ${uat_ebiller} \n 👉 ${uat_fileserver} \n 👉 ${uat_gateway} \n 👉 ${uat_ncde} \n 👉 ${uat_PaygateHVM} \n 👉 ${uat_ptiapps} \n 👉 ${uat_ptipaygate} \n 👉 ${uat_recon} \n 👉 ${uat_webterminal}</i>"'
                }
            }
        }
        stage('Reboot Approval') {
            steps {
                echo '===========Push Notification: Approval==========='
                withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                string(credentialsId: 'telegramChatID', variable: 'CHAT_ID')]) {
                    sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                    -d chat_id=${CHAT_ID} \
                    -d parse_mode="HTML" \
                    -d text="<b>Pause! Automated Reboot for UAT Servers for approval.</b>"'

                    script {
                        def proceed = true
                        try {
                            timeout(time: 3600, unit: 'SECONDS') {
                                input('Do you want to proceed for production deployment?')
                            }
                            sh 'echo "Deployment to production will resume"'
                        } catch (err) {
                            proceed = false
                            currentBuild.result = 'Deny'
                        }
                        if (proceed) {
                            echo '=========== PROCEED ==========='
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>Servers Reboot for UAT all server, Approved!</b>"'
                        } else {
                            echo '=========== DONT PROCEED ==========='
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>Servers Reboot Permission Denied! Approval is needed.</b>"'
                            currentBuild.result = 'Deny'
                            error('Stopping early…')
                        }
                    }
                }
            }
        }
        stage('Server Reboot') {
            steps {
                echo '=========== SERVER REBOOT ============'
                script {
                    withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                    string(credentialsId: 'telegramChatID', variable: 'CHAT_ID')]) {
                        try {
                            echo '=========== REBOOT: ${uat_bis} ============'
                          //  sh 'aws ec2 reboot-instances --instance-ids ${id_uat_bis}'
                          //  sleep(30)
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_bis} reboot completed! Server is running.</b>"'
                        } catch (Exception e) {
                            echo '=========== REBOOT Failure ==========='
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_bis} server reboot, Failed!</b>"'
                            currentBuild.result = 'ABORTED'
                            error('Stopping early…')
                        }

                        try {
                            echo '=========== REBOOT: ${uat_cde} ============'
                         //   sh 'aws ec2 reboot-instances --instance-ids ${id_uat_cde}'
                         //   sleep(30)
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_cde} reboot completed! Server is running.</b>"'
                        } catch (Exception e) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_cde} server reboot, Failed!</b>"'
                            currentBuild.result = 'ABORTED'
                            error('Stopping early…')
                        }

                        try {
                            echo '=========== REBOOT: ${uat_ebiller} ============'
                         //   sh 'aws ec2 reboot-instances --instance-ids ${id_uat_ebiller}'
                         //   sleep(30)
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_ebiller} reboot completed! Server is running.</b>"'
                        } catch (Exception e) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_ebiller} server reboot, Failed!</b>"'
                            currentBuild.result = 'ABORTED'
                            error('Stopping early…')
                        }

                        try {
                            echo '=========== REBOOT: ${uat_fileserver} ============'
                         //   sh 'aws ec2 reboot-instances --instance-ids ${id_uat_fileserver}'
                         //   sleep(30)
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_fileserver} reboot completed! Server is running.</b>"'
                        } catch (Exception e) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_fileserver} server reboot, Failed!</b>"'
                            currentBuild.result = 'ABORTED'
                            error('Stopping early…')
                        }

                        try {
                            echo '=========== REBOOT: ${uat_gateway} ============'
                          //  sh 'aws ec2 reboot-instances --instance-ids ${id_uat_gateway}'
                          //  sleep(30)
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_gateway} reboot completed! Server is running.</b>"'
                        } catch (Exception e) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_gateway} server reboot, Failed!</b>"'
                            currentBuild.result = 'ABORTED'
                            error('Stopping early…')
                        }

                        try {
                            echo '=========== REBOOT: ${uat_ncde} ============'
                         //   sh 'aws ec2 reboot-instances --instance-ids ${id_uat_ncde}'
                         //   sleep(30)
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_ncde} reboot completed! Server is running.</b>"'
                        } catch (Exception e) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_ncde} server reboot, Failed!</b>"'
                            currentBuild.result = 'ABORTED'
                            error('Stopping early…')
                        }

                        try {
                            echo '=========== REBOOT: ${uat_PaygateHVM} ============'
                         //   sh 'aws ec2 reboot-instances --instance-ids ${id_uat_PaygateHVM}'
                         //   sleep(30)
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_PaygateHVM} reboot completed! Server is running.</b>"'
                        } catch (Exception e) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_PaygateHVM} server reboot, Failed!</b>"'
                            currentBuild.result = 'ABORTED'
                            error('Stopping early…')
                        }

                        try {
                            echo '=========== REBOOT: ${uat_ptiapps} ============'
                         //   sh 'aws ec2 reboot-instances --instance-ids ${id_uat_ptiapps}'
                         //   sleep(30)
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_ptiapps} reboot completed! Server is running.</b>"'
                        } catch (Exception e) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_ptiapps} server reboot, Failed!</b>"'
                            currentBuild.result = 'ABORTED'
                            error('Stopping early…')
                        }

                        try {
                            echo '=========== REBOOT: ${uat_ptipaygate} ============'
                         //   sh 'aws ec2 reboot-instances --instance-ids ${id_uat_ptipaygate}'
                         //   sleep(30)
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_ptipaygate} reboot completed! Server is running.</b>"'
                        } catch (Exception e) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_ptipaygate} server reboot, Failed!</b>"'
                            currentBuild.result = 'ABORTED'
                            error('Stopping early…')
                        }

                        try {
                            echo '=========== REBOOT: ${uat_recon} ============'
                         //   sh 'aws ec2 reboot-instances --instance-ids ${id_uat_recon}'
                         //   sleep(30)
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_recon} reboot completed! Server is running.</b>"'
                        } catch (Exception e) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_recon} server reboot, Failed!</b>"'
                            currentBuild.result = 'ABORTED'
                            error('Stopping early…')
                        }

                        try {
                            echo '=========== REBOOT: ${uat_webterminal} ============'
                         //   sh 'aws ec2 reboot-instances --instance-ids ${id_uat_webterminal}'
                         //   sleep(30)
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_webterminal} reboot completed! Server is running.</b>"'
                        } catch (Exception e) {
                            sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                            -d chat_id=${CHAT_ID} \
                            -d parse_mode="HTML" \
                            -d text="<b>${uat_webterminal} server reboot, Failed!</b>"'
                            currentBuild.result = 'ABORTED'
                            error('Stopping early…')
                        }
                    }
                }
            }
        }
        stage('Push Notification: End') {
            steps {
                echo '=========== Notification: Reboot Success ============'
                withCredentials([string(credentialsId: 'telegramToken', variable: 'TOKEN'),
                string(credentialsId: 'telegramChatID', variable: 'CHAT_ID')]) {
                    sh 'curl -s -X POST https://api.telegram.org/bot${TOKEN}/sendMessage \
                    -d chat_id=${CHAT_ID} \
                    -d parse_mode="HTML" \
                    -d text="<b>Congratulations! UAT Servers Reboot on Paynamics Account ID are successfully Completed!</b>"'
                }
            }
        }
    }
}
