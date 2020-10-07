// Declarative Pipeline
pipeline{
	// agent any
	agent{
		label "node"
	}
	stages{
		stage("A"){
			steps{
				echo "Starting checkout..."
				checkout([$class: 'GitSCM', branches: [[name: '*/master']], userRemoteConfigs: [[url: 'http://git-server/user/repository.git']]])
			}
			post{
				always{
					echo "========always========"
				}
				success{
					echo "========A executed successfully========"
				}
				failure{
					echo "========A execution failed========"
				}
			}
		}
	}
	post{
		always{
			echo "========always========"
		}
		success{
			echo "========pipeline executed successfully ========"
		}
		failure{
			echo "========pipeline execution failed========"
		}
	}
}