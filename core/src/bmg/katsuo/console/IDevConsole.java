package bmg.katsuo.console;

import bmg.katsuo.IApplication;

public interface IDevConsole
{
	void Create(IApplication app);
	
	void Log(String message);
	
	void Error(String message);
	
	void Resize(int width, int height);

	void SetDebug(boolean debug);

	void Render();
	
	void Update(float delta);
	
	void Open();
	
	void Close();
	
	void Clear();
	
	void AddCommand(ConsoleCommand command);
	
	void RemoveCommand(String command);
	
	void ExecuteCommand(String command);

	ConsoleCommand GetCommand(String command);
	
	boolean IsOpen();
	
	void dispose();

	void Pause();
	void Resume();
}
