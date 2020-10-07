// Declarative Pipeline
def GIT_COMMIT_HASH

pipeline{
	environment {
    	registry = "kicaj29/hello-world-image"
    	registryCredential = 'dockerhub'
		dockerImage = ''
  	}
	// agent any
	agent{
		label "builder-node-mounted"
	}
	stages{
		stage("Source code checkout"){
			steps{
				echo "Starting checkout..."
				checkout([$class: 'GitSCM', branches: [[name: '*/master']], userRemoteConfigs: [[url: 'https://github.com/kicaj29/jenkins-pipelines']]])
				script{
					GIT_COMMIT_HASH = sh (script: "git log -n 1 --pretty=format:'%h'", returnStdout: true)
					echo "checkout from commit: ${GIT_COMMIT_HASH}"
				}
			}
			post{
				/*always{
				}*/
				success{
					echo "Source code checkout - success"
				}
				failure{
					echo "Source code checkout - fail"
				}
			}
		}
		stage("test") {
			failFast true
			parallel {
				stage("test on node:4.6") {
					agent {
						docker {image 'node:4.6'}
					}
					steps{
						echo "starting tests on node 4.6 ..."
						sh 'node --version'
						sh 'npm install --only=dev'
						sh 'npm test'
						// after executing npms the container is deleted
					}
				}
				stage("test on node:latest") {
					agent {
						docker {image 'node:latest'}
					}
					steps{
						echo "starting tests on latest node ..."
						sh 'node --version'
						sh 'npm install --only=dev'
						sh 'npm test'
						// after executing npms the container is deleted
					}
				}
			}
		}
		stage("build docker image"){
			steps{
				script {
          			dockerImage = docker.build registry + ":$GIT_COMMIT_HASH"
        		}
			}
		}
		stage("publish docker image"){
			steps{
				script {
          			docker.withRegistry('https://index.docker.io/v1/', registryCredential) {
            			dockerImage.push()
          			}
        		}
			}
		}
	}
	post{
		/*always{			
		}*/
		success{
			echo "========pipeline executed successfully ========"
		}
		failure{
			echo "========pipeline execution failed========"
		}
	}
}