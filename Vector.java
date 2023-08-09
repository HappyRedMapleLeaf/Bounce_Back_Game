/**
 * represents a 2D vector and provides vector operations
 */
public class Vector {
  // vector components
  public float x;
  public float y;

  /**
   * creates a vector with x and y components
   *
   * @param x x-component
   * @param y y-component
   */
  public Vector(float x, float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Creates a zero vector
   */
  public Vector() {
    x = 0;
    y = 0;
  }

  /**
   * Calculates the dot product between this vector and another vector
   *
   * @param v The other vector
   * @return The dot product of the two vectors
   */
  public float dot(Vector v) {
    // multiplying components and summing them
    return x * v.x + y * v.y;
  }

  /**
   * Multiplies vector by a scalar
   *
   * @param k scalar factor
   * @return resulting vector
   */
  public Vector multScalar(float k) {
    // multiply each component by the scalar value
    return new Vector(x * k, y * k);
  }

  /**
   * Finds vector projection of this vector onto another one
   *
   * @param v The vector to project onto
   * @return Vector projection
   */
  public Vector projectOn(Vector v) {
    if (v.magnitude() == 0.0f) {
      // if vector has magnitude 0, return a zero vector to avoid dividing by 0
      return new Vector();
    }
    // [ (a dot b) / (b dot b) ] * b
    return v.multScalar(this.dot(v) / v.dot(v));
  }

  /**
   * Gets the magnitude (length) of the vector
   *
   * @return magnitude of the vector
   */
  public float magnitude() {
    // Calculate magnitude with Pythagorean theorem
    return (float) Math.sqrt(x * x + y * y);
  }

  /**
   * Normalizes the vector (same direction, magnitude 1)
   *
   * @return the normalized vector
   */
  public Vector norm() {
    float magnitude = this.magnitude();
    if (magnitude == 0.0f) {
      // If the magnitude is 0, return a zero vector to avoid dividing by 0
      return new Vector();
    } else {
      // Normalize the vector by dividing each component by the magnitude
      return this.multScalar(1.0f / magnitude);
    }
  }

  /**
   * Adds another vector to this vector
   *
   * @param v vector to add
   * @return resulting vector after addition
   */
  public Vector add(Vector v) {
    // Add components
    return new Vector(x + v.x, y + v.y);
  }

  /**
   * Subtracts another vector from this vector
   *
   * @param v vector to subtract
   * @return resulting vector after subtraction
   */
  public Vector subtract(Vector v) {
    // Subtract components
    return new Vector(x - v.x, y - v.y);
  }

  /**
   * Returns string representation of the vector
   *
   * @return formatted string for the vector in the form [x, y]
   */
  public String toString() {
    return String.format("[%f, %f]", x, y);
  }
}