# encoding: UTF-8
require "rubygems"
require "bundler/setup"
require "json"
require "mongoid"
require 'redis'

require_relative "lib/error_information"
require_relative 'lib/stormalog_st'
#$stderr.reopen(File.expand_path(File.join(File.dirname(__FILE__), "error.log")), "a")

Mongoid.logger = Logger.new("mongoid_st.log")
ENV["MONGOID_ENV"] ||= "development"
Mongoid.load!("config/mongoid.yml")
st = StormalogST.new("../../production.log")
Thread.new {
  sleep 10
  st.running = false
}
st.run

