// Declarative Pipeline
pipeline{
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
					def GIT_COMMIT_HASH = sh (script: "git log -n 1 --pretty=format:'%H'", returnStdout: true)
					echo "checkout from commit: ${GIT_COMMIT_HASH}"
				}
			}
			post{
				always{
					
				}
				success{
					echo "Source code checkout - success"
				}
				failure{
					echo "Source code checkout - fail"
				}
			}
		}
	}
	post{
		always{			
		}
		success{
			echo "========pipeline executed successfully ========"
		}
		failure{
			echo "========pipeline execution failed========"
		}
	}
}