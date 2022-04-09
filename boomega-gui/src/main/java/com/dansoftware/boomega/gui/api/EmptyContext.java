/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.api;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface EmptyContext extends Context {
    @Override
    default void showOverlay(Region region, boolean blocking) {

    }

    @Override
    default void hideOverlay(Region region) {

    }

    @Override
    @NotNull
    default ContextDialog showErrorDialog(String title, String message, Consumer<ButtonType> onResult) {
        return null;
    }

    @Override
    @NotNull
    default ContextDialog showErrorDialog(String title, String message, Exception cause, Consumer<ButtonType> onResult) {
        return null;
    }

    @Override
    @NotNull
    default ContextDialog showInformationDialog(String title, String message, Consumer<ButtonType> onResult) {
        return null;
    }

    @Override
    default ContextDialog showConfirmationDialog(String title, String message, Consumer<ButtonType> onResult) {
        return null;
    }

    @Override
    @NotNull
    default ContextDialog showDialog(String title, Node content, Consumer<ButtonType> onResult, ButtonType... buttonTypes) {
        return null;
    }

    @Override
    default ButtonType showErrorDialogAndWait(String title, String message) {
        return null;
    }

    @Override
    default ButtonType showErrorDialogAndWait(String title, String message, Exception e) {
        return null;
    }

    @Override
    default ButtonType showInformationDialogAndWait(String title, String message) {
        return null;
    }

    @Override
    default ButtonType showConfirmationDialogAndWait(String title, String message) {
        return null;
    }

    @Override
    default ButtonType showDialogAndWait(String title, Node content, ButtonType... buttonTypes) {
        return null;
    }

    @Override
    default void showErrorNotification(String title, String message) {

    }

    @Override
    default void showErrorNotification(String title, String message, EventHandler<MouseEvent> onClicked) {

    }

    @Override
    default void showErrorNotification(String title, String message, Duration duration) {

    }

    @Override
    default void showErrorNotification(String title, String message, Duration duration, EventHandler<MouseEvent> onClicked) {

    }

    @Override
    default void showWarningNotification(String title, String message) {

    }

    @Override
    default void showWarningNotification(String title, String message, EventHandler<MouseEvent> onClicked) {

    }

    @Override
    default void showWarningNotification(String title, String message, Duration duration) {

    }

    @Override
    default void showWarningNotification(String title, String message, Duration duration, EventHandler<MouseEvent> onClicked) {

    }

    @Override
    default void showInformationNotification(String title, String message) {

    }

    @Override
    default void showInformationNotification(String title, String message, EventHandler<MouseEvent> onClicked) {

    }

    @Override
    default void showInformationNotification(String title, String message, Duration duration) {

    }

    @Override
    default void showInformationNotification(String title, String message, Duration duration, EventHandler<MouseEvent> onClicked) {

    }

    @Override
    default void showInformationNotification(String title, String message, EventHandler<MouseEvent> onClicked, Hyperlink... hyperlinks) {

    }

    @Override
    @Nullable
    default Scene getContextScene() {
        return null;
    }

    @Override
    @Nullable
    default Window getContextWindow() {
        return null;
    }

    @Override
    default void focusRequest() {

    }

    @Override
    default void toFrontRequest() {

    }

    @Override
    default boolean isShowing() {
        return false;
    }

    @Override
    default void showIndeterminateProgress() {

    }

    @Override
    default void stopProgress() {

    }

    @Override
    default void showProgress(long done, long max, @NotNull ProgressType type) {

    }

    @Override
    default void onWindowPresent(Consumer<Window> action) {

    }

    @NotNull
    @Override
    default ObservableList<Region> getBlockingOverlaysShown() {
        return FXCollections.observableArrayList();
    }

    @NotNull
    @Override
    default ObservableList<Region> getNonBlockingOverlaysShown() {
        return FXCollections.observableArrayList();
    }

    @Override
    default void showOverlay(@NotNull Region region) {
        Context.super.showOverlay(region);
    }

    @Override
    default void showErrorDialog(@NotNull String title, @NotNull String message) {
        Context.super.showErrorDialog(title, message);
    }

    @Override
    default void showErrorDialog(@NotNull String title, @NotNull String message, @Nullable Exception e) {
        Context.super.showErrorDialog(title, message, e);
    }

    @Override
    default void sendRequest(@NotNull Context.Request request) {
        Context.super.sendRequest(request);
    }

    @Override
    default boolean isReachable() {
        return Context.super.isReachable();
    }

    @Override
    default void close() {
        Context.super.close();
    }
}
