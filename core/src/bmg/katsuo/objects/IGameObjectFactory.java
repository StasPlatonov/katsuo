package bmg.katsuo.objects;

public interface IGameObjectFactory
{
	/**
	 * retrieve a game object of the given name
	 */
	GameObject GetObject(String name);

	/**
	 * register a object getter by the object name
	 * @param objectname
	 * @param getter
	 */
	void RegisterObject(String objectname, IGameObjectGetter getter);
}
