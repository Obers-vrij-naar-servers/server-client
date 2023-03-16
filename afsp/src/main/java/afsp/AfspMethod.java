package afsp;

public enum AfspMethod {
    LIST,GET,POST,DELETE;
    public static final int MAX_LENGTH;

    static {
        int _maxValue = -1;
        for (AfspMethod method :values()){
            if (method.name().length() > _maxValue){
                _maxValue = method.name().length();
            }
        }
        MAX_LENGTH = _maxValue;
    }
}