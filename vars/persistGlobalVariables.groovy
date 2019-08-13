def call(variables) {
  def json=groovy.json.JsonOutput.toJson(variables)
  writeFile(file: 'globalVariables', text: json)
  stash name: 'globalVariablesStash', includes: 'globalVariables'
}
