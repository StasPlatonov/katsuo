package bmg.katsuo.utils;

import java.util.Comparator;

public interface Priority
{
	int GetPriority();
	
	class PriorityComparator implements Comparator<Priority>
    {
		@Override
		public int compare(Priority o1, Priority o2) {
			return Integer.compare(o2.GetPriority(), o1.GetPriority());
		}
		
	}
}
