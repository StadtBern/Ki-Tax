/**
 * This interface should be implemented by a Controller when this wants to provide a way to be automatically focused
 * when getting back to it.
 */
export interface IDVFocusableController {

    /**
     * This funktion must be called to set the focus back to the given element.
     */
    setFocusBack(elementID: string): void;

}
