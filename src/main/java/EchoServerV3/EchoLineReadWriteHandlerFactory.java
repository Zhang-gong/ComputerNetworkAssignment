package EchoServerV3;
public class EchoLineReadWriteHandlerFactory implements ISocketReadWriteHandlerFactory {
	public IReadWriteHandler createHandler() {
		return new EchoLineReadWriteHandler();
	}
}
