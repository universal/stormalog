class ErrorInformation
  include Mongoid::Document

  field :type, type: String
  field :stacktrace, type: String
  field :request, type: String 
end