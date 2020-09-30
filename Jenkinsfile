// def: allows you to declare variables
def commit_id

pipeline { 
	agent any
	stages {
		stage('Preparation') {
			steps {
				// read commit ID (sha) from the HEAD and store its value in a file .git/commit-id
				sh 'git rev-parse --short HEAD > .git/commit-id'
				// save this commit ID in variable commit_id
				commit_id = readFile('.git/commit-id').trim()
			}
		}
		stage('test') {
			steps {
				// nodejs: https://www.jenkins.io/doc/pipeline/steps/nodejs/
				nodejs(nodeJSInstallationName: 'nodejs') {
					sh 'npm install --only=dev'
					sh 'npm test'
				}
			}
		}
		stage('docker build/push') {
			// 'dockerhub': is ID of credentials defined in Jenkins
			docker.withRegistry('https://index.docker.io/v1/', 'dockerhub') {
				def app = docker.build("kicaj29/hello-world-image:${commit_id}", '.').push()
			}
		}
	}
}