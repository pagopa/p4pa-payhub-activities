package it.gov.pagopa.payhub.activities.exception;

/**
 * A custom exception that represents errors related to activities within an application.
 * This exception extends {@link RuntimeException}, allowing it to be thrown during runtime
 * without requiring explicit declaration in the method signature.
 *
 * <p>
 * The {@code ActivitiesException} is designed to provide a meaningful message about
 * what caused the exception, helping developers to debug and understand issues in the
 * activity processing flow.
 * </p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * if (someConditionFails) {
 *     throw new ActivitiesException("Activity failed due to some condition.");
 * }
 * }</pre>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Extends {@code RuntimeException} for unchecked exception behavior.</li>
 *   <li>Accepts a detailed message to describe the nature of the error.</li>
 * </ul>
 *
 * @see RuntimeException
 */
public class ActivitiesException extends RuntimeException {

    /**
     * Constructs a new {@code ActivitiesException} with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception.
     */
    public ActivitiesException(String message) {
        super(message);
    }
}
