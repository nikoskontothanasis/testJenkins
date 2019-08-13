long call() {
  def date=java.util.Calendar.getInstance()
  def currentTime=date.getTimeInMillis()
  
  return currentTime
}
