public class SubscribeModel {
	
	private String event;
	private ChannelObject params;
	
	
	public static class ChannelObject {
		
		private String channel;
		private String cb_id;
		public String getChannel() {
			return channel;
		}
		
		public void setChannel(String channel) {
			this.channel = channel;
		}
		
		public String getCb_id() {
			return cb_id;
		}
		
		public void setCb_id(String cb_id) {
			this.cb_id = cb_id;
		}
		
	}


	public String getEvent() {
		return event;
	}


	public void setEvent(String event) {
		this.event = event;
	}


	public ChannelObject getParams() {
		return params;
	}


	public void setParams(ChannelObject params) {
		this.params = params;
	}
	
}