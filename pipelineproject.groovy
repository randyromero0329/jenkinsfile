pipeline {
  agent any

  stages {
    stage('Push Notification: Start') {
      steps {
        echo '=========== Notify on Telegram Bot : Build started ============'
        script {
          sh 'curl -s -X POST https://api.telegram.org/bot1938619252:AAGklXh-j5V4cOvo1r2ayu9XElPtQ13RTc8/sendMessage -d chat_id=1686082402 -d parse_mode="HTML" -d text="<b>Hello! Build started on campfire-api-dev</b>"'

        }
      }
    }
    stage('GitLab Checkout: API') {
      steps {
        echo '=========== Gitlab Checkout: api & paste env to api.env ============'
      }
    }
    stage('Remove temp folder') {
      steps {
        echo '=========== Remove temporary directory ============'
      }
    }
    stage('Docker Build') {
      steps {
        echo '=========== Docker Build ============'
      }
    }
    stage('Docker Push to ECR') {
      steps {
        echo '=========== Docker push to ECR repository ============'
      }
    }
    stage('Docker Push to ECS') {
      steps {
        echo '=========== Docker push to ECS cluster tasks ============'
      }
    }
    stage('Remove Unused docker image') {
      steps {
        echo '=========== Remove unused/old docker images ============'
      }
    }
    stage('Push Notification: Finished') {
      steps {
        echo '=========== Notify on Telegram Bot : Build successful ============'
      }
    }
  }
}
