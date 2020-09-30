- [install jenkins using docker](#install-jenkins-using-docker)
- [review amd use demo application](#review-amd-use-demo-application)
  - [install nodejs plugin in Jenkins](#install-nodejs-plugin-in-jenkins)
  - [create first jenkins job and configure it manually](#create-first-jenkins-job-and-configure-it-manually)
  - [Run the job](#run-the-job)
- [Use docker to build nodejs app](#use-docker-to-build-nodejs-app)
  - [Install docker plugin on jenkins](#install-docker-plugin-on-jenkins)
  - [Add docker client to the jenkins image](#add-docker-client-to-the-jenkins-image)
    - [Create new image that contains also docker client](#create-new-image-that-contains-also-docker-client)
    - [Run container from the new image](#run-container-from-the-new-image)
    - [Connect to the new container as root and check if docker client is connected to the docker engine.](#connect-to-the-new-container-as-root-and-check-if-docker-client-is-connected-to-the-docker-engine)
    - [Connect to the new container as jenkins user and check of docker client is connected to the docker engine.](#connect-to-the-new-container-as-jenkins-user-and-check-of-docker-client-is-connected-to-the-docker-engine)
  - [Create docker image as build output](#create-docker-image-as-build-output)
    - [Add new build step: docker build and publish](#add-new-build-step-docker-build-and-publish)
    - [Create new repo in docker hub](#create-new-repo-in-docker-hub)
    - [Configure the new step](#configure-the-new-step)
    - [Remove not needed step](#remove-not-needed-step)
    - [Run the job to build and publish the image](#run-the-job-to-build-and-publish-the-image)
    - [Pull and run created image](#pull-and-run-created-image)
- [Infrastructure as a code](#infrastructure-as-a-code)
  - [Jenkins Job DSL (Domain Specific Language)](#jenkins-job-dsl-domain-specific-language)
    - [Install DSL plugin](#install-dsl-plugin)
    - [Create new DSL job](#create-new-dsl-job)
    - [Configure DSL job](#configure-dsl-job)
- [resources](#resources)

# install jenkins using docker

It looks that now this is official image for Jenkins: https://www.jenkins.io/blog/2018/12/10/the-official-Docker-image/

```
docker run --name myjenkins -p 8777:8080 -p 50000:50000 -v D:\dockershare\jenkins_home:/var/jenkins_home jenkins/jenkins:lts
```
* After installation add all default plugins.
* Create admin user and click on the form **Save and Continue**.

At this point in time the lts image contained **Jenkins 2.249.1**.

# review amd use demo application

It is simple nodejs application.

[app-sources](./app-sources)

## install nodejs plugin in Jenkins

Manage Jenkins -> Manage Plugins -> Available -> type in search ```nodejs``` -> select from the list NodeJs plugin -> click 'Download now and install after restart'.

![nodejsplugin](./images/jenkins-install-nodejs-plugin.png)

[pluing page](https://plugins.jenkins.io/nodejs/)

**This will NOTE install nodejs application on the jenkins host** but it will allow to install it later.

After installation the plugin should be on the list of installed plugins.
![installednodejs](./images/installed-nodejs-plugin.png)

## create first jenkins job and configure it manually

Create `nodejs example app` as `freestyle project`.   

1. Define link to git repository.
![jenkins-job-manual-config-step1-source-code-mgmt.png](./images/jenkins-job-manual-config-step1-source-code-mgmt.png)
2. Add build step to execute shell command.
![jenkins-job-manual-config-step2-create-shell-command.png](./images/jenkins-job-manual-config-step2-create-shell-command.png)
![jenkins-job-manual-config-step3-npm-install.png](./images/jenkins-job-manual-config-step3-npm-install.png)
3. Save jenkins job configuration.
4. Install nodejs   
   Manage Jenkins -> Global Tool Configuration
![jenkins-job-manual-config-step4-install-nodejs.png](./images/jenkins-job-manual-config-step4-install-nodejs.png)
5. Go back to the job configuration to point installed nodejs
![jenkins-job-manual-config-step5-set-path-to-nodejs.png](./images/jenkins-job-manual-config-step5-set-path-to-nodejs.png)

## Run the job
The job now can successfully download all needed npm packages.
![jenkins-job-manual-config-step6-run-the-job.png](./images/jenkins-job-manual-config-step6-run-the-job.png)
All job output is available in mounted folder: `D:\dockershare\jenkins_home\workspace\nodejs example app`.

```ps
PS D:\dockershare\jenkins_home\workspace\nodejs example app\app-sources> npm start

> myapp@0.0.1 start D:\dockershare\jenkins_home\workspace\nodejs example app\app-sources
> node index.js

Example app listening at http://:::3000
```

# Use docker to build nodejs app

## Install docker plugin on jenkins
Manage Jenkins -> Manage Plugins -> Available -> type docker in search input. Click **Download now and install after restart**.

![jenkins-docker-plugin](./images/jenkins-docker-plugin.png)

## Add docker client to the jenkins image

Because jenkins is already running in docker container we need to make sure that this jenkins container can access docker socket which is in linux system a file that can be used to communicate with docker api. Basically we need to make sure that docker client works correctly in the in the jenkins container - it means it can connect with docker engine. More [here](https://stackoverflow.com/questions/35110146/can-anyone-explain-docker-sock/35110344#:~:text=130-,docker.,defaults%20to%20use%20UNIX%20socket.&text=There%20might%20be%20different%20reasons,Docker%20socket%20inside%20a%20container.).

### Create new image that contains also docker client

>NOTE: To check docker group ID run ```cat /etc/group | grep docker``` or alternatively ```getent group docker``` in docker engine host (or client??? I am not sure). **After some testing it looks that this ID can be any not used group ID at least in combination Win10 and Linux Container!**.

```ps
PS D:\GitHub\kicaj29\jenkins-pipelines\jenkins-docker> docker build -t jenkins-docker:ver1 .
Sending build context to Docker daemon  2.048kB
Step 1/4 : FROM jenkins/jenkins:lts
 ---> 190554e5446b
Step 2/4 : USER root
 ---> Running in d1bbcda974b3
Removing intermediate container d1bbcda974b3
 ---> 62c4070a94b7
Step 3/4 : RUN mkdir -p /tmp/download &&  curl -L https://download.docker.com/linux/static/stable/x86_64/docker-18.03.1-ce.tgz | tar -xz -C /tmp/download &&  rm -rf /tmp/download/docker/dockerd &&  mv /tmp/download/docker/docker* /usr/local/bin/ &&  rm -rf /tmp/download &&  groupadd -g 106 docker &&  usermod -aG staff,docker jenkins
 ---> Running in 9ac716a7ee59
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 36.9M  100 36.9M    0     0  8777k      0  0:00:04  0:00:04 --:--:-- 8779k
Removing intermediate container 9ac716a7ee59
 ---> 85ad9aca58cd
Step 4/4 : USER jenkins
 ---> Running in 320d8c8600c3
Removing intermediate container 320d8c8600c3
 ---> df06b72d3428
Successfully built df06b72d3428
Successfully tagged jenkins-docker:ver1
SECURITY WARNING: You are building a Docker image from Windows against a non-Windows Docker host. All files and directories added to build context will have '-rwxr-xr-x' permissions. It is recommended to double check and reset permissions for sensitive files and directories.
```

Next we can stop and remove previous container with jenkins and run new container from the new image.

### Run container from the new image

```
PS D:\GitHub\kicaj29\jenkins-pipelines\jenkins-docker> docker run -p 8777:8080 -p 50000:50000 -v D:\dockershare\jenkins_home:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock --name jenkins-docker -u jenkins jenkins-docker:ver1
```
>NOTE: [here](https://nagachiang.github.io/docker-bind-to-docker-socket-on-windows-host-from-linux-containers/#) is information to map socket in this way ```docker run -v //var/run/docker.sock:/var/run/docker.sock ...``` (double slash at the beginning and this version also works fine).

>NOTE1: it is important to run ```docker run``` as jenkins user (```-u jenkins```). Without this jenkins user will not have permission to ```docker.sock``` socket. This was the case in scenario Win10 host and DockerDesktopVM.

### Connect to the new container as root and check if docker client is connected to the docker engine.
```
PS D:\> docker exec -it -u root jenkins-docker bash
root@6df5f0604e38:/# docker ps
```
You can run also ```docker version``` in Windows 10 and in the container to see if information about docker engine is exactly the same.

We can also check permissions:
```
root@17907514d865:/# ls -ahl /var/run/docker.sock
srw-rw---- 1 root docker 0 Sep 28 04:59 /var/run/docker.sock
```

### Connect to the new container as jenkins user and check of docker client is connected to the docker engine.

```
PS D:\> docker exec -it jenkins-docker bash
jenkins@17907514d865:/$ docker ps
```

## Create docker image as build output

### Add new build step: docker build and publish

![jenkins-job-manual-config-step7-step-docker-build-publish.png](images/jenkins-job-manual-config-step7-step-docker-build-publish.png)

### Create new repo in docker hub

![jenkins-job-manual-config-step7-new-repo.png](images/jenkins-job-manual-config-step7-new-repo.png)

### Configure the new step

![jenkins-job-manual-config-step8-docker-step-config.png](images/jenkins-job-manual-config-step8-docker-step-config.png)

In advanced settings set project root folder:
![jenkins-job-manual-config-step9-folder-root.png](images/jenkins-job-manual-config-step9-folder-root.png)

### Remove not needed step

Because now npm install is executed in [dockerfile](./app-sources/Dockerfile) we can delete step create [earlier](#create-first-jenkins-job-and-configure-it-manually) (step that was doing also ```npm install```).

### Run the job to build and publish the image

After run the image should be available in docker hub.
![new image](./images/jenkins-job-manual-config-step10-new-image.png)


### Pull and run created image

```ps
PS D:\> docker run -p 3001:3000 -d --name my-nodejs-app kicaj29/hello-world-image:latest
b2de148fdea652c4c1f02763896c470ba7e9f0f54d75481fdf628ef49deca801
PS D:\> docker ps | sls hello-world-image

b2de148fdea6        kicaj29/hello-world-image:latest   "docker-entrypoint.sΓÇª"   36 seconds ago      Up 35 seconds
      0.0.0.0:3001->3000/tcp                             my-nodejs-app
```

Next we can open the app in web browser:
![jenkins-job-manual-config-step11-run-app.png](images/jenkins-job-manual-config-step11-run-app.png)

# Infrastructure as a code

Issues when not using IaC:
* no proper audit trail
* no easy history of changes
* segregation between Jenkins admins and developers
  * users (often developers) will have to contact a Jenkins administrator to make a changes
  * Long lead times for changes
* Difficult to backup and restore (e.g. how to restore just one setting to how it was the day before)

All the above problems do not appear when using IaC.

## Jenkins Job DSL (Domain Specific Language)

DSL is a jenkins plugin that allows you to defined jobs in a programmatic form. You can describe jobs using **Groovy** based language.

### Install DSL plugin

Manage Jenkins -> Manage Plugins
![jenkins-job-manual-config-step12-dsl-plugin-install.png](images/jenkins-job-manual-config-step12-dsl-plugin-install.png)

### Create new DSL job

Select freestyle project job type.
![jenkins-job-manual-config-step13-new-dsl-job.png](images/jenkins-job-manual-config-step13-new-dsl-job.png)


### Configure DSL job

Define repo:
![jenkins-job-manual-config-step14-dsl-config-source-code.png](images/jenkins-job-manual-config-step14-dsl-config-source-code.png)

Create new build step:   
![jenkins-job-manual-config-step15-dsl-new-build-step.png](images/jenkins-job-manual-config-step15-dsl-new-build-step.png)

Configure the step:
![jenkins-job-manual-config-step16-dsl-configure-step.png](images/jenkins-job-manual-config-step16-dsl-configure-step.png)

# resources
https://github.com/wardviaene/jenkins-course   
https://github.com/wardviaene/docker-demo   
https://www.udemy.com/course/learn-devops-ci-cd-with-jenkins-using-pipelines-and-docker/   
https://github.com/wardviaene/jenkins-docker   