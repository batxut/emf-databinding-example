package org.eclipse.emf.example.library.service.xmi.test1;

import java.io.File;

import org.eclipse.emf.example.library.service.xmi.XMILibraryPersistenceService;

public class Test1PersistenceService extends XMILibraryPersistenceService {

	@Override
	public File doGetFile() {
		return new File(System.getProperty("user.home")+"/lib_1.xmi");
	}

	public String getIdentifier() {
		return Activator.PLUGIN_ID + "test1";
	}

	public String getLabel() {
		return "lib_1.xmi";
	}

	public String getToolTip() {
		return doGetFile().getAbsolutePath();
	}

}
