// node: influence on what jenkins worker node the job will be ran (here: any node)
node { 
	// def: allows you to declare variables
	def commit_id	
	stage('Preparation') {
		// checkout scm: will do git clone
		checkout scm	 		
		// read commit ID (sha) from the HEAD and store its value in a file .git/commit-id
		sh "git rev-parse --short HEAD > .git/commit-id"              
		// save this commit ID in variable commit_id
		commit_id = readFile('.git/commit-id').trim()	 
	}
	stage('test') {
		nodejs(nodeJSInstallationName: 'nodejs') {
		sh 'cd app-sources'
		sh 'npm install --only=dev'
		sh 'npm test'
		}
	}
	stage('docker build/push') {
		// 'dockerhub': is ID of credentials defined in Jenkins
		docker.withRegistry('https://index.docker.io/v1/', 'dockerhub') {
		def app = docker.build("kicaj29/hello-world-image:${commit_id}", '.').push()
     }
   }
}