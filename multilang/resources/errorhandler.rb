# encoding: UTF-8
require "rubygems"
require "bundler/setup"
require "json"
require "mongoid"

require "./lib/storm"
require "./lib/error_handler_bolt"
require "./lib/error_information"

#$stderr.reopen(File.expand_path(File.join(File.dirname(__FILE__), "error.log")), "a")

Mongoid.logger = Logger.new("/Users/jhedtrich/development/repositories/ec2/seminar_software_performance_engineering/stormalog/mongoid.log")
ENV["MONGOID_ENV"] ||= "development"
Mongoid.load!("config/mongoid.yml")
ErrorInformation.delete_all
ErrorHandlerBolt.new.run