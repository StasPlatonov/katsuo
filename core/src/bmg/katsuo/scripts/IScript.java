package bmg.katsuo.scripts;

public interface IScript
{
    boolean Reload();
    boolean CanExecute();
    boolean Init(Object... objects);
    boolean Init(String moduleName, Object... objects);
    boolean ExecuteFunction(String functionName, Object... objects);
    boolean ExecuteFunction(String moduleName, String functionName, Object... objects);
}
