//Get persisted job files from master node
def call(persistedFilesWorkspaceDir, isFullBuild) {
  node('master') {
    //Create directory outside of the workspace if it does not exist, and stash any files found there
    script {
      def persistedFilesPathOnMaster="${env.JENKINS_HOME}/jobsPersistedFiles/${env.JOB_NAME}"
      
      def persistedFilesMasterDir=new File(persistedFilesPathOnMaster)
      def persistedFilesPathOnNode="${env.WORKSPACE}/${persistedFilesWorkspaceDir}"
      
      //Clear the directory from the master node's workspace
      dir(persistedFilesWorkspaceDir) {
        deleteDir()
      }
      
      //Copy directory contents to the master node's workspace. Skip this step for full build
      if (persistedFilesMasterDir.exists() && !isFullBuild) {
        org.apache.commons.io.FileUtils.copyDirectory(persistedFilesMasterDir, new File(persistedFilesPathOnNode))
      }
    }
    
    dir(persistedFilesWorkspaceDir) {
      //Stash the dir contents
      stash includes: '**', name: 'jobPersistedFilesStash', allowEmpty: true
      //Delete directory from master's workpace
      deleteDir()
    }
  }
  
  dir(persistedFilesWorkspaceDir) {
    deleteDir()
  }
  
  dir(persistedFilesWorkspaceDir) {
    echo "Unstashing the job\'s persisted files to node \"${env.NODE_NAME}\""
    unstash 'jobPersistedFilesStash'
    writeFile(file: 'buildNumber', text: env.BUILD_NUMBER)
  }
}