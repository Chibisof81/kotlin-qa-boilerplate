@Library("qa-pipeline-library") _

pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '30'))
        timestamps()
        timeout(time: 1, unit: 'HOURS')
    }

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['dev', 'stage', 'prod'],
            description: 'Target environment'
        )
        string(
            name: 'TAGS',
            defaultValue: 'testcontainers,chrome',
            description: 'Test tags (comma separated): testcontainers,selenoid,local,chrome,firefox,edge,smoke,regression'
        )
        string(
            name: 'BROWSER_MODE',
            defaultValue: 'testcontainers',
            description: 'Browser mode: local, selenoid, testcontainers'
        )
        string(
            name: 'BROWSER_NAME',
            defaultValue: 'chrome',
            description: 'Browser name: chrome, firefox, edge'
        )
        string(
            name: 'UI_BASE_URL',
            defaultValue: 'https://dev.example.com',
            description: 'UI base URL'
        )
        string(
            name: 'API_BASE_URL',
            defaultValue: 'https://dev-api.example.com',
            description: 'API base URL'
        )
        booleanParam(
            name: 'RUN_LINT',
            defaultValue: true,
            description: 'Run ktlint before tests'
        )
    }

    stages {
        stage('Lint') {
            when {
                expression { return params.RUN_LINT }
            }
            steps {
                dir('tests') {
                    sh '''
                        ./gradlew ktlintCheck --no-daemon
                    '''
                }
            }
        }

        stage('Test') {
            steps {
                dir('tests') {
                    sh '''
                        ./gradlew test \
                            --no-daemon \
                            -Dtags=${TAGS} \
                            -Denv=${ENVIRONMENT} \
                            -Dbrowser.mode=${BROWSER_MODE} \
                            -Dbrowser.name=${BROWSER_NAME} \
                            -Dui.base.url=${UI_BASE_URL} \
                            -Dapi.base.url=${API_BASE_URL}
                    '''
                }
            }
            post {
                always {
                    junit testResults: 'tests/build/test-results/**/*.xml', allowMissingResults: true
                    allure includeProperties: false, results: [[path: 'tests/build/reports/allure-results']], properties: [[key: 'Environment', value: "${ENVIRONMENT}"]]
                }
            }
        }
    }

    post {
        always {
            cleanWs(cleanWhenNotBuilt: false, deleteDirs: true, notFailBuild: true)
        }
        failure {
            echo "Pipeline failed! Check Allure report for details."
        }
    }
}