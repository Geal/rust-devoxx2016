#[macro_use] extern crate lazy_static;
extern crate regex;
extern crate libc;

use std::ffi::CStr;
use std::str;
use regex::Regex;
use libc::c_char;

use std::collections::{HashMap,HashSet};

pub struct Index {
  pub index: HashMap<String, HashSet<i32>>,
}

lazy_static! {
  static ref RE: Regex = Regex::new(r"[:punct:]").unwrap();
}

impl Index {
  pub fn new() -> Index {
    Index {
      index: HashMap::new(),
    }
  }

  pub fn insert(&mut self, id: i32, data: &str) {
    for word in data.split_whitespace() {
      let w = RE.replace_all(word, "").to_lowercase();

      if self.index.contains_key(&w) {
        self.index.get_mut(&w).map(|h| h.insert(id));
      } else {
        let mut h = HashSet::new();
        h.insert(id);
        self.index.insert(w, h);
      }
    }
  }

  pub fn search_word(&self, word: &str) -> Option<&HashSet<i32>> {
    self.index.get(word)
  }

  pub fn search(&self, text: &str) -> HashSet<i32> {
    let mut split = text.split_whitespace();

    let res: HashSet<i32> = if let Some(Some(h)) = split.next().map(|word| {
      let w = RE.replace_all(&word, "").to_lowercase();
      self.search_word(&w)
    }) {
      h.clone()
    } else {
      HashSet::new()
    };

    split.fold(res, |set, ref word| {
      let w = RE.replace_all(&word, "").to_lowercase();
      self.index.get(&w).map(|h| h.intersection(&set).cloned().collect()).unwrap_or(HashSet::new())
    })
  }
}

pub struct SearchResult {
  data: Vec<i32>
}

#[no_mangle]
pub extern fn index_create() -> Box<Index> {
  Box::new(Index::new())
}

#[no_mangle]
pub extern fn index_free(_: Box<Index>) {

}

#[no_mangle]
pub extern fn index_insert(index: &mut Index, id: i32, raw_text: *const c_char) {
  let slice = unsafe { CStr::from_ptr(raw_text).to_bytes() };
  let text = str::from_utf8(slice).unwrap();
  index.insert(id, text);
}

#[no_mangle]
pub extern fn index_search(index: &Index, raw_text: *const c_char) -> Box<SearchResult> {
  let slice = unsafe { CStr::from_ptr(raw_text).to_bytes() };
  let text = str::from_utf8(slice).unwrap();
  let h = index.search(text);
  let v: Vec<i32> = h.iter().cloned().collect();

  Box::new(SearchResult {
    data: v
  })
}

#[no_mangle]
pub extern fn search_result_count(search: &SearchResult) -> i32 {
  search.data.len() as i32
}

#[no_mangle]
pub extern fn search_result_get(search: &SearchResult, index: i32) -> i32 {
  *search.data.get(index as usize).unwrap()
}

#[no_mangle]
pub extern fn search_result_free(_: Box<SearchResult>) {

}

#[test]
fn try_to_index() {
  let mut index = Index::new();
  index.insert(0, "Mary had a little lambda,
 Its syntax white as snow,
 And every program Mary wrote,
 She wrote in Lisp, you know.");
  index.insert(1, "Mary had a little lambda.
 She wore it on her blouse.
 And everywhere that Mary moved
 She'd reconstruct the house.");
  index.insert(2, "Mary had a little lambda
 Its value was n times n plus 1,
 And everywhere that Mary went
 She was in a quantum state.");
  index.insert(3, "Mary had a little lambda
 A sheep she couldn't clone
 And everywhere that lambda went
 Her calculus got blown.");

  println!("index: {:?}", index.index);

  let mary_set:HashSet<i32>              = [1, 2, 3, 0].iter().cloned().collect();
  let house_set:HashSet<i32>             = [1].iter().cloned().collect();
  let sheep_set:HashSet<i32>             = [3].iter().cloned().collect();
  let everywhere_set:HashSet<i32>        = [1, 2, 3].iter().cloned().collect();
  let sheep_everywhere_set: HashSet<i32> = sheep_set.intersection(&everywhere_set).cloned().collect();

  assert_eq!(index.search("mary"),   mary_set);
  assert_eq!(index.search("house"),  house_set);
  assert_eq!(index.search("house."), house_set);
  assert_eq!(index.search("sheep"),  sheep_set);
  assert_eq!(index.search("everywhere"), everywhere_set);
  assert_eq!(index.search("sheep everywhere"), sheep_everywhere_set);
  assert_eq!(index.search("everywhere  sheep"), sheep_everywhere_set);
}
