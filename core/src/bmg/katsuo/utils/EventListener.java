package bmg.katsuo.utils;

public interface EventListener<T>
{
	void Process(Event<T> event, T object);
}
