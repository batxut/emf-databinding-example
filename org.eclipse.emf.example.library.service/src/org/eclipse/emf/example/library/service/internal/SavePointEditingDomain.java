/*******************************************************************************
 * Copyright (c) 2009 BestSolution.at and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.example.library.service.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.OverrideableCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.example.library.service.ISavePoint;
import org.eclipse.emf.example.library.service.ISavePointEditingDomain;

public class SavePointEditingDomain implements EditingDomain, ISavePointEditingDomain {
	private EditingDomain wrappedDomain;
	private List<SavePoint> savePoints;
	private boolean skipEvent;
	
	public static class SavePoint implements ISavePoint {
		private String name;
		private int count;
		private SavePointEditingDomain domain;
		private boolean disposed;
		
		SavePoint(SavePointEditingDomain domain, String name) {
			this.domain = domain;
			this.name = name;
		}

		public void dispose() {
			disposed = true;
			domain.removeSavePoint(this);
		}

		public void rollback() {
			domain.rollbackToSavePoint(this);
		}
		
		public boolean canRollback() {
			return ! disposed;
		}
	}
	
	private CommandStackListener stackListener = new CommandStackListener() {

		public void commandStackChanged(EventObject event) {
			if( savePoints != null && ! skipEvent ) {
				for( SavePoint p : savePoints ) {
					p.count++;
				}
			}
		}
		
	};

	public SavePointEditingDomain(EditingDomain wrappedDomain) {
		this.wrappedDomain = wrappedDomain;
	}
	
	public Command createCommand(Class<? extends Command> commandClass,
			CommandParameter commandParameter) {
		return wrappedDomain.createCommand(commandClass, commandParameter);
	}

	public Command createOverrideCommand(OverrideableCommand command) {
		return wrappedDomain.createOverrideCommand(command);
	}

	public Resource createResource(String fileNameURI) {
		return wrappedDomain.createResource(fileNameURI);
	}

	public Collection<?> getChildren(Object object) {
		return wrappedDomain.getChildren(object);
	}

	public Collection<Object> getClipboard() {
		return wrappedDomain.getClipboard();
	}

	public CommandStack getCommandStack() {
		return new CommandStack() {

			public void addCommandStackListener(CommandStackListener listener) {
				wrappedDomain.getCommandStack().addCommandStackListener(listener);
			}

			public boolean canRedo() {
				return wrappedDomain.getCommandStack().canRedo();
			}

			public boolean canUndo() {
				return wrappedDomain.getCommandStack().canUndo();
			}

			public void execute(Command command) {
				wrappedDomain.getCommandStack().execute(command);
			}

			public void flush() {
				wrappedDomain.getCommandStack().flush();
			}

			public Command getMostRecentCommand() {
				return wrappedDomain.getCommandStack().getMostRecentCommand();
			}

			public Command getRedoCommand() {
				return wrappedDomain.getCommandStack().getRedoCommand();
			}

			public Command getUndoCommand() {
				return wrappedDomain.getCommandStack().getUndoCommand();
			}

			public void redo() {
				try {
					skipEvent = true;
					wrappedDomain.getCommandStack().redo();
					if( savePoints != null ) {
						for( SavePoint p : savePoints ) {
							p.count++;
						}
					}
				} finally {
					skipEvent = false;
				}
				
			}

			public void removeCommandStackListener(CommandStackListener listener) {
				wrappedDomain.getCommandStack().removeCommandStackListener(listener);
			}

			public void undo() {
				try {
					skipEvent = true;
					wrappedDomain.getCommandStack().undo();
					if( savePoints != null ) {
						ArrayList<SavePoint> points = new ArrayList<SavePoint>(savePoints);
						
						for( SavePoint p : points ) {
							p.count--;
							if( p.count == 0 ) {
								p.dispose();
							}
						}
					}
				} finally {
					skipEvent = false;
				}
			}
			
		};
	}

	public Collection<?> getNewChildDescriptors(Object object, Object sibling) {
		return wrappedDomain.getNewChildDescriptors(object, sibling);
	}

	public boolean getOptimizeCopy() {
		return wrappedDomain.getOptimizeCopy();
	}

	public Object getParent(Object object) {
		return wrappedDomain.getParent(object);
	}

	public ResourceSet getResourceSet() {
		return wrappedDomain.getResourceSet();
	}

	public Object getRoot(Object object) {
		return wrappedDomain.getRoot(object);
	}

	public List<?> getTreePath(Object object) {
		return wrappedDomain.getTreePath(object);
	}

	public boolean isControllable(Object object) {
		return wrappedDomain.isControllable(object);
	}

	public boolean isReadOnly(Resource resource) {
		return wrappedDomain.isReadOnly(resource);
	}

	public Resource loadResource(String fileNameURI) {
		return wrappedDomain.loadResource(fileNameURI);
	}

	public void setClipboard(Collection<Object> clipboard) {
		wrappedDomain.setClipboard(clipboard);
	}

	public TreeIterator<?> treeIterator(Object object) {
		return wrappedDomain.treeIterator(object);
	}

	private boolean rollbackToSavePoint(SavePoint savepoint) {
		if( savePoints != null ) {
			if( savepoint != null ) {
				int steps = savepoint.count;
				CommandStack stack = getCommandStack();
				if( steps > 0 ) {
					for( int i = 0; i < steps; i++ ) {
						if( stack.canUndo() ) {
							stack.undo();	
						} else {
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	public ISavePoint addSavePoint(String savepoint) {
		if( savePoints == null ) {
			savePoints = new ArrayList<SavePoint>();
			getCommandStack().addCommandStackListener(stackListener);
		}
		
		SavePoint p;
		if( (p = getSavePoint(savepoint)) == null ) {
			p = new SavePoint(this,savepoint);
			savePoints.add(p);			
		}

		return p;
	}

	private void removeSavePoint(SavePoint savepoint) {
		if( savePoints != null ) {
			savePoints.remove(savepoint);
			if( savePoints.size() == 0 ) {
				savePoints = null;
				getCommandStack().removeCommandStackListener(stackListener);
			}
		}
	}

	public SavePoint getSavePoint(String savepoint) {
		if( savePoints != null ) {
			for( SavePoint p : savePoints ) {
				if( p.name.equals(savepoint) ) {
					return p;
				}
			}
		}
		
		return null;
	}
}