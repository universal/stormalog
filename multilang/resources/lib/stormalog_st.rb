class StormalogST
  attr_reader :requests, :redis, :processed
  attr_accessor :running

  def initialize(source)
    read_requests(source)
    @running = true
    @redis = Redis.new(:host => 'localhost', :port => 6379)
    @redis.del "actioncount"
    ErrorInformation.delete_all
  end

  def run
    @current = 0
    @processed = 0
    while running
      handle_request next_request
      @processed += 1
    end
  end
private
  def next_request
    previous = @current
    @current = (@current + 1)% self.requests.size
    self.requests[previous]
  end

  def handle_request request
    if error_request? request
      handle_error_request request
    else
      handle_successful_request request
    end
  end

  def handle_successful_request request
    action = if (md = request.match(/.*Processing by ([:\w]+\#\w+).*/m))
      md[1]
    else
      "notmatched"
    end
    self.redis.hincrBy "actioncount", action, 1
  end

  def handle_error_request request
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
  end

  def error_request? request
    request.include?("Completed 500") || !request.include?("Completed")
  end

  def read_requests(source)
    @requests = []
    first = false
    request = ""
    File.open(source) do |f|
      f.each do |line|
        if line.match /^Started .*/
          (@requests << request) if first
          request = line
          first = true
        else
          request << line
        end
        request << "\n"
      end
    end
  end
end