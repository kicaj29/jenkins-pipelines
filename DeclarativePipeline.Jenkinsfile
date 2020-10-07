// Declarative Pipeline
def GIT_COMMIT_HASH

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
			steps{
				// it is possible also to install multiple nodejs version in jenkins but then we have to manage it
        		// so that`s why it is easier to run the test in dedicated container        
        		def myTestContainer = docker.image('node:4.6')
        		// call pull to make sure that the newest version is used (connect with docker hub) and do not use cached version
        		myTestContainer.pull()
        		// now execute npm in this new container, thx to this I do not need nodejs in my jenkins container
        		myTestContainer.inside {
            		sh 'npm install --only=dev'
            		sh 'npm test'
        		}
        		// after executing npms the container is deleted and next stage starts
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