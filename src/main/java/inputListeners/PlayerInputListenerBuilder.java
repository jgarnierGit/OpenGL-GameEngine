package inputListeners;

public class PlayerInputListenerBuilder {
	
	private PlayerInputListenerBuilder() {
		//hidden constructor
	}
	
	public static class EmptyPlayerListenerBuilder {
		private MouseInputListener mouseInputHandler;
		private KeyboardInputListener keyboardInputHandler;

		public EmptyPlayerListenerBuilder addMouseInputListener() {
			mouseInputHandler = new MouseInputListener();
			return this;
		}
		
		public EmptyPlayerListenerBuilder addKeyboardInputListener() {
			keyboardInputHandler = new KeyboardInputListener();
			return this;
		}

		public PlayerInputListener build() {
			PlayerInputListener playerInputListener = new PlayerInputListener();
			playerInputListener.keyboardInputHandler = this.keyboardInputHandler;
			playerInputListener.mouseInputHandler = this.mouseInputHandler;
			return playerInputListener;
		}
	}

	public static EmptyPlayerListenerBuilder create() {
		return new EmptyPlayerListenerBuilder();
	}

}
