package protocol;

public class ProtocolPackage {
    public ProtocolCommand command;
    public String[] arguments;

    public ProtocolPackage(ProtocolCommand command, String[] arguments) {
        this.command = command;
        this.arguments = arguments;
    }
}
