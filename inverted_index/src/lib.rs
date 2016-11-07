use std::collections::{HashMap,HashSet};

pub struct Index {
  pub index: HashMap<String, HashSet<i32>>,
}

impl Index {
  pub fn new() -> Index {
    Index {
      index: HashMap::new(),
    }
  }

  pub fn insert(&mut self, id: i32, data: &str) {
    
  }

  pub fn search_word(&self, word: &str) -> Option<&HashSet<i32>> {
    self.index.get(word)
  }
}
