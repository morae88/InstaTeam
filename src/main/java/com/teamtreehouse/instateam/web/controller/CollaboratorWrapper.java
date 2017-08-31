package com.teamtreehouse.instateam.web.controller;

import com.teamtreehouse.instateam.model.Collaborator;

import java.util.List;

public class CollaboratorWrapper {
    public List<Collaborator> collaborators;

    public CollaboratorWrapper(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    public CollaboratorWrapper() {
    }

    public List<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }
}
