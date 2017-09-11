/**
 * This interface should be implemented by a Controller when this wants to provide a way to be automatically focused
 * when getting back to it.
 */
export interface IDVFocusableController {

    setFocusBack(): void;

}
