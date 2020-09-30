job('NodeJS Docker example') {
    scm {
        git('git://github.com/kicaj29/jenkins-pipelines') {  node -> // is hudson.plugins.git.GitSCM
            node / gitConfigName('DSL User')
            node / gitConfigEmail('dsluser@gmail.com')
        }
    }
    triggers {
        scm('H/5 * * * *')
    }
    wrappers {
        nodejs('nodejs') // this is the name of the NodeJS installation in 
                         // Manage Jenkins -> Configure Tools -> NodeJS Installations -> Name
    }
    steps {
        dockerBuildAndPublish {
            repositoryName('kicaj29/hello-world-image')
            tag('${GIT_REVISION,length=9}')		//if it would be empty then latest tag would be used
            registryCredentials('dockerhub')	//create credentials with 'dockerhub' as ID
			buildContext('app-sources')			//project root folder (here dockerfile should be placed)
            forcePull(false)
            forceTag(false)
            createFingerprints(false)
            skipDecorate()
        }
    }
}