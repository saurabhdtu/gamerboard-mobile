extension IterableExtensions<T> on Iterable<T> {
  T? elementAtOrNull(int index) {
    if(index < this.length){
      return this.elementAt(index);
    }
    return null;
  }
}