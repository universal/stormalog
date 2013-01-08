class ErrorHandlerBolt < Storm::Bolt
  def process(tup)
    request = tup.values.first
    error_information = ErrorInformation.new
    parts = request.split(/^$/)
    error_information.request = parts.first
    error_information.stacktrace = parts[1]

    if request.include? "ActionController::RoutingError"
      error_information.type = "ActionController::RoutingError"
    elsif request.include?("Completed 500") && (match = parts[1].match /([\w:]+) .*/)
      error_information.type = match[1]
    end
    error_information.save
    ack tup
  end
end
