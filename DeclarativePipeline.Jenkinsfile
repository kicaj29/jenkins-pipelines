// Declarative Pipeline
pipeline{
	// agent any
	agent{
		label "builder-node-mounted"
	}
	stages{
		stage("A"){
			steps{
				echo "Starting checkout..."
				checkout([$class: 'GitSCM', branches: [[name: '*/master']], userRemoteConfigs: [[url: 'https://github.com/kicaj29/jenkins-pipelines']]])
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