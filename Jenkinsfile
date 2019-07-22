pipeline {
    agent any
	stages {
		stage('Build'){
			steps {
			    withAnt(installation: 'Ant Installation') {
			        bat label: '', script: 'C:\\Users\\nkontotha\\Documents\\DevOps\\ESB\\antScripts\\buildModules.bat'
			        }
		    	}
	    	}
    	}
    }   