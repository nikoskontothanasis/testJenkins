//Persisted job files to master node
def call(persistedFilesWorkspaceDir) {
  //Stash the files of the main execution's node
  dir(persistedFilesWorkspaceDir) {
    echo "Stashing the job\'s persisted files from node \"${env.NODE_NAME}\""
    stash includes: '**', name: 'jobPersistedFilesStash', allowEmpty: true
  }
  
  node('master') {
    //Create directory outside of the workspace if it does not exist, and unstash any files found there
    script {
      def persistedFilesPathOnMaster="${env.JENKINS_HOME}/jobsPersistedFiles/${env.JOB_NAME}"
      
      def persistedFilesMasterDir=new File(persistedFilesPathOnMaster)
      def persistedFilesPathOnNode="${env.WORKSPACE}/${persistedFilesWorkspaceDir}"
      
      //Clear the directory from the master node's workspace
      dir(persistedFilesWorkspaceDir) {
        deleteDir()
      }
      
      dir(persistedFilesWorkspaceDir) {
        echo "Unstashing the job\'s persisted files to master node"
        unstash 'jobPersistedFilesStash'
        writeFile(file: 'buildNumber', text: env.BUILD_NUMBER)
      }
      
      //Backup the job's previously persisted files
      /*
      if (persistedFilesMasterDir.exists()) {
        def backupDirPath="${env.JENKINS_HOME}/jobsPersistedFiles_Backups/${env.JOB_NAME}/beforeBuild${env.BUILD_NUMBER}"
        def backupDir=new File(backupDirPath)
        
        org.apache.commons.io.FileUtils.moveDirectory(persistedFilesMasterDir, backupDir)
        
        assert (!persistedFilesMasterDir.exists()) && backupDir.exists(): "The directory \"${persistedFilesPathOnMaster}\" was not successfully moved to \"${backupDirPath}\" on master node"
      }
      */
      
      //Clear the job's persisted files directory
      if (persistedFilesMasterDir.exists()) {
        persistedFilesMasterDir.deleteDir()
        assert !persistedFilesMasterDir.exists(): "The directory \"${persistedFilesPathOnMaster}\" was not successfully deleted on master node"
      }
      
      persistedFilesMasterDir.mkdirs()
      
      assert persistedFilesMasterDir.exists(): "The directory \"${persistedFilesPathOnMaster}\" was not successfully created on master node"
      
      //Copy directory contents from the master node's workspace to the persisted files directory
      org.apache.commons.io.FileUtils.copyDirectory(new File(persistedFilesPathOnNode), persistedFilesMasterDir)
    }
  }
}