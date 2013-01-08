class HomeController < ApplicationController
  def index
    @actioncount = $redis.hgetall "actioncount"
    respond_to do |format|
      format.html
      format.js
    end
  end
end
