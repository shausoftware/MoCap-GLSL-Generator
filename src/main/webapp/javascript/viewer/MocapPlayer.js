'use strict';

import React from 'react';
import { useEffect } from 'react';

import PlaybackController from './toolbars/PlaybackController';
import Viewer from './Viewer';
import ViewParameters from './toolbars/ViewParameters';
import Offset from './toolbars/Offset';
import Joints from './toolbars/side/Joints';
import SaveProject from './SaveProject';
import OpenProject from './OpenProject';
import Import from './Import';
import Analyse from './toolbars/side/Analyse';
import Fourier from './toolbars/Fourier';
import Help from './toolbars/side/Help';

const MocapPlayer = (props) =>  {

    const API_VERSION = "1.0.6";
    const emptyScene = {filename: '', frames: [], bounds: {minX: 0, minY: 0, minZ: 0, maxX: 0, maxY: 0, maxZ : 0}};
    const defaultOffset = {jointId: undefined, x: '', y: '', z: ''};
    const defaultPlaybackParameters = {startFrame: 0,
                                       endFrame: 0,
                                       useLoopEasing: false,
                                       loopEasingFrames: 0,
                                       frameDuration: 128,
                                       scale: 1.0,
                                       view: "XY"};

    const [scene, setScene] = React.useState(emptyScene);
    const [playbackParameters, setPlaybackParameters] = React.useState(defaultPlaybackParameters);
    const [offset, setOffset] = React.useState(defaultOffset);

    const [playing, setPlaying] = React.useState(false);
    const [currentFrame, setCurrentFrame] = React.useState(0);
    const [jointDataFrame, setJointDataFrame] = React.useState(0);
    const [showDialogState, setShowDialogState] = React.useState({viewDialog: false,
                                                                  offsetDialog: false,
                                                                  jointDialog: false,
                                                                  axisDialog: false,
                                                                  saveProjectDialog: false,
                                                                  openProjectDialog: false,
                                                                  importDialog: false,
                                                                  analyseDialog: false,
                                                                  fourierDialog: false,
                                                                  helpDialog: false});
    const [showStats, setShowStats] = React.useState(true);
    const [updateProps, setUpdateProps] = React.useState(false); //TODO: this forces update of React properties to popups/toolbars. It's ugly

    const importScene = (newScene) => {
        setScene(newScene);
        let newPlaybackParameters = {...defaultPlaybackParameters, endFrame: newScene.frames.length};
        setPlaybackParameters(newPlaybackParameters);
        setOffset(defaultOffset);
        openDialog('importDialog'); //close
    }

    const openProject = (newProject) => {
        //newProject = updateProjectApis(newProject);
        setScene(newProject.scene);
        setPlaybackParameters(newProject.playbackParameters);
        setCurrentFrame(parseInt(newProject.playbackParameters.startFrame));
        setOffset(newProject.offset);
        openDialog('openProjectDialog'); //close
    }

    //TODO: handle future updates to api versioning
    const updateProjectApis = (newProject) => {
    }

    const openDialog = (dialog) => {
        let newDialogState = Object.assign({}, showDialogState);
        Object.entries(newDialogState).forEach((entry) => {
            const [key, value] = entry;
            if (key == dialog) {
                let nextState = !newDialogState[key];
                if (nextState) {
                    //force property update when opening
                    setUpdateProps(true);
                }
                newDialogState[key] = nextState;
            } else {
                newDialogState[key] = false;
            }
        });
        setShowDialogState(newDialogState);
    }

    const updatePlaybackParameters = (parameters, values) => {
        let newPlaybackParameters = Object.assign({}, playbackParameters);
        for (var i = 0; i < parameters.length; i++) {
            if (newPlaybackParameters.hasOwnProperty(parameters[i])) {
                newPlaybackParameters[parameters[i]] = values[i];
            }
        }
        setPlaybackParameters(newPlaybackParameters);
    }

    const openJointDialog = () => {
        setJointDataFrame(currentFrame);
        openDialog('jointDialog');
    }

    const openStats = () => {
        setShowStats(!showStats);
    }

    const updatePlaying = () => {
        setPlaying(!playing);
    }

    const updateCurrentFrame = (frame) => {
        if (frame && !isNaN(frame) && frame >= 0 && frame < scene.frames.length) {
            setCurrentFrame(parseInt(frame));
        }
    }

    const updateJoint = (frameId, jointId, display, colour,
                         x, y, z, globalX, globalY, globalZ) => {

        let newScene = Object.assign({}, scene);
        newScene.frames = newScene.frames.map((frame) => {
            frame.joints = frame.joints.map((joint) => {
                if (jointId === joint.id) {
                    joint.display = display;
                    joint.colour = colour;
                    if (globalX || frameId == frame.id - 1) {
                        joint.x =  x;
                    }
                    if (globalY || frameId == frame.id - 1) {
                        joint.y = y;
                    }
                    if (globalZ || frameId == frame.id - 1) {
                        joint.z = z;
                    }
                }
                return joint;
            });
            return frame;
        });
        setScene(newScene);
    }

    const setAsCenterJoint = (jointId) => {
        setOffset({jointId: jointId, x: '', y: '', z: ''});
    }

    const setOffsetCoordinates = (x, y, z) => {
        setOffset({jointId: undefined, x: x, y: y, z: z});
    }

    useEffect(() => {
        let interval = null;
        if (playing) {
            interval = setInterval(() => {
                setCurrentFrame(currentFrame < playbackParameters.endFrame - 1 ? currentFrame + 1 : parseInt(playbackParameters.startFrame));
            }, playbackParameters.frameDuration);
        } else if (!playing) {
            clearInterval(interval);
        }
        return () => clearInterval(interval);
    }, [playing, currentFrame]);

    return(
        <div className="container-fluid h-100 w-100 p-0 bg-dark">
            <ViewParameters showDialogState={showDialogState}
                            openDialog={openDialog}
                            playbackParameters={playbackParameters}
                            updatePlaybackParameters={updatePlaybackParameters}
                            totalFrames={scene.frames.length}
                            updateProps={updateProps}
                            setUpdateProps={setUpdateProps}
                            />
            <Offset showDialogState={showDialogState}
                    openDialog={openDialog}
                    offset={offset}
                    setOffsetCoordinates={setOffsetCoordinates}
                    setAsCenterJoint={setAsCenterJoint}
                    updateProps={updateProps}
                    setUpdateProps={setUpdateProps}
                    />
            <Joints showDialogState={showDialogState}
                       openDialog={openDialog}
                       scene={scene}
                       jointDataFrame={jointDataFrame}
                       updateJoint={updateJoint}
                       updateProps={updateProps}
                       setUpdateProps={setUpdateProps}
                       />
            <SaveProject showDialogState={showDialogState}
                         openDialog={openDialog}
                         filename={scene.filename}
                         apiVersion={API_VERSION}
                         playbackParameters={playbackParameters}
                         offset={offset}
                         scene={scene}
                         updateProps={updateProps}
                         setUpdateProps={setUpdateProps}
                         />
            <OpenProject showDialogState={showDialogState}
                         openDialog={openDialog}
                         openProject={openProject}
                         updateProps={updateProps}
                         setUpdateProps={setUpdateProps}
                         />
            <Import showDialogState={showDialogState}
                    openDialog={openDialog}
                    importScene={importScene}
                    updateProps={updateProps}
                    setUpdateProps={setUpdateProps}
                    />
            <Fourier showDialogState={showDialogState}
                     openDialog={openDialog}
                     updateProps={updateProps}
                     setUpdateProps={setUpdateProps}
                     scene={scene}
                     playbackParameters={playbackParameters}
                     offset={offset}
                     />
            <PlaybackController sceneLoaded={scene.frames.length > 0}
                                playing={playing}
                                updatePlaying={updatePlaying}
                                currentFrame={currentFrame}
                                updateCurrentFrame={updateCurrentFrame}
                                playbackParameters={playbackParameters}
                                updatePlaybackParameters={updatePlaybackParameters}
                                openDialog={openDialog}
                                openJointDialog={openJointDialog}
                                openStats={openStats}
                                />
            <Analyse showDialogState={showDialogState}
                     openDialog={openDialog}
                     scene={scene}
                     />
            <Viewer scene={scene}
                    playbackParameters={playbackParameters}
                    currentFrame={currentFrame}
                    offset={offset}
                    totalFrames={scene.frames.length}
                    showStats={showStats}
                    />
            <Help showDialogState={showDialogState}
                  openDialog={openDialog}
                  apiVersion={API_VERSION}
                  />
        </div>
    );
}

export default MocapPlayer;
