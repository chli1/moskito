package net.anotheria.moskito.core.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MoSKito Context is a thread local class, that is used to contain some information about current execution, for example tags.
 *
 * @author lrosenberg
 * @since 22.05.17 01:36
 */
public class MoSKitoContext {

	private static InheritableThreadLocal<MoSKitoContext> currentContext = new InheritableThreadLocal<MoSKitoContext>(){

		@Override
		protected MoSKitoContext childValue(MoSKitoContext parentValue) {
			MoSKitoContext child = new MoSKitoContext();
			child.reset();
			child.tags.putAll(getTags());
			return child;
		}

		@Override
		protected MoSKitoContext initialValue() {
			return new MoSKitoContext();
		}
	};


	public static MoSKitoContext get(){
		return currentContext.get();
	}

	/**
	 * Map with tags assigned to this context.
	 */
	private HashMap<String, String> tags = new HashMap<>();

	private HashSet<Integer> seenErrors = new HashSet<>();

	/**
	 * If true an error has occured in this thread already. This is useful to separate from initial errors in the processing and followup errors.
	 */
	private AtomicBoolean errorOccured = new AtomicBoolean(false);

	public static void addTag(String tagName, String tagValue){
		get().tags.put(tagName, tagValue);
	}

	public static Map<String, String> getTags(){
		return (Map<String, String>) get().tags.clone();
	}

	/**
	 * Marks current context as erroneous (having one error occurred already) and returns the previous value. This method is threadsafe.
	 * @return
	 */
	public boolean markErrorAndReturnIfErrorAlreadyHappenedBefore(){
		return errorOccured.getAndSet(true);
	}

	/**
	 * Resets the current context. Especially useful for unit-testing.
	 */
	public void reset(){
		tags = new HashMap<>();
		errorOccured = new AtomicBoolean(false);
		seenErrors = new HashSet<>();
	}


	public static void cleanup() {
		get().reset();
		currentContext.remove();
	}

	public boolean seenErrorAlready(Throwable throwable) {
		Integer key = getHashKey(throwable);
		if (seenErrors.contains(key))
			return true;
		seenErrors.add(key);
		return false;
	}

	/* test visibility */ static Integer getHashKey(Throwable t){
		return t.hashCode();
	}

}
