pipeline {
    agent any
    tools {
        jdk "jdk-21"
        maven "maven-3.9"
    }
    triggers {
        pollSCM "H H * * *"
    }
    parameters {
        booleanParam(name: "CLEAN", description: "Clean workspace.", defaultValue: false)
        booleanParam(name: "RELEASE", description: "Release that thang.", defaultValue: false)
    }
    options {
        skipDefaultCheckout(true) // This is required if you want to clean before build
    }    
    stages {
        stage('Info') {
            steps {
                echo '=== Stage: Info ==='
                sh 'java -version'
                sh 'mvn -version'
            }
        }
    	stage('Clean') {
        	when {expression { params.RELEASE || params.CLEAN }}    	
    		steps {
    			echo "=== Stage: Clean ==="
    			cleanWs()
    		}
    	}
    	stage("Checkout") {
    		steps {
    			echo "=== Stage: Checkout ==="
    			checkout scm
    		}
    	}
        stage('Snapshot') {
        	when {expression { !params.RELEASE }}        
            steps {
                echo "=== Stage: Snapshot (Build & Deploy) ==="
                sh 'mvn clean deploy docker:build docker:push -Ddkr.tag=snapshot'
            }
        }
        stage('Release') {
        	when {expression { params.RELEASE }}
            steps {
                echo "=== Stage: Release (Build & Deploy) ==="
                sh 'mvn -B release:prepare release:perform -Darguments=-Ddkr.tag=stable'
            }
        }
    }
}
